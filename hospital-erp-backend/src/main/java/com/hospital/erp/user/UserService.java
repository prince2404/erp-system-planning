package com.hospital.erp.user;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.PageResponse;
import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.user.dto.BulkUserRow;
import com.hospital.erp.user.dto.PermissionRequest;
import com.hospital.erp.user.dto.PermissionResponse;
import com.hospital.erp.user.dto.UserCreateRequest;
import com.hospital.erp.user.dto.UserResponse;
import com.hospital.erp.user.dto.UserUpdateRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final CenterRepository centerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final RankGuard rankGuard;

    public PageResponse<UserResponse> list(Role role, Long centerId, Boolean active, Pageable pageable) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (centerId != null) {
                predicates.add(cb.equal(root.get("center").get("id"), centerId));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (actor.getScopeType() == ScopeType.CENTER) {
                predicates.add(cb.equal(root.get("center").get("id"), actor.getScopeId()));
            }
            if (actor.getScopeType() == ScopeType.BLOCK) {
                predicates.add(cb.equal(root.get("center").get("block").get("id"), actor.getScopeId()));
            }
            if (actor.getScopeType() == ScopeType.DISTRICT) {
                predicates.add(cb.equal(root.get("center").get("district").get("id"), actor.getScopeId()));
            }
            if (actor.getScopeType() == ScopeType.STATE) {
                predicates.add(cb.equal(root.get("center").get("state").get("id"), actor.getScopeId()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return PageResponse.from(userRepository.findAll(spec, pageable).map(UserResponse::from));
    }

    public UserResponse get(Long id) {
        assertUserManager(currentUserService.get());
        return UserResponse.from(findUser(id));
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        rankGuard.assertCanCreate(actor, request.role());
        return UserResponse.from(createInternal(request, actor));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        User user = findUser(id);
        rankGuard.assertCanManage(actor, user);

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.role() != null) {
            rankGuard.assertCanCreate(actor, request.role());
            user.setRole(request.role());
            user.setRank(request.role().getRank());
        }
        if (request.centerId() != null) {
            user.setCenter(findCenter(request.centerId()));
        }
        if (request.scopeType() != null) {
            user.setScopeType(request.scopeType());
        }
        if (request.scopeId() != null) {
            user.setScopeId(request.scopeId());
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }
        User saved = userRepository.save(user);
        replacePermissions(saved, request.permissionIds(), actor);
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse deactivate(Long id) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        User user = findUser(id);
        rankGuard.assertCanManage(actor, user);
        user.setActive(false);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public List<UserResponse> bulkConfirm(List<UserCreateRequest> requests) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        return requests.stream()
                .map(request -> {
                    rankGuard.assertCanCreate(actor, request.role());
                    return UserResponse.from(createInternal(request, actor));
                })
                .toList();
    }

    public List<BulkUserRow> previewBulk(MultipartFile file) {
        assertUserManager(currentUserService.get());
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<BulkUserRow> rows = new ArrayList<>();
            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (row == null) {
                    continue;
                }
                rows.add(parseBulkRow(row, index + 1));
            }
            return rows;
        } catch (IOException ex) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Unable to read Excel file");
        }
    }

    public byte[] template() {
        assertUserManager(currentUserService.get());
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");
            Row header = sheet.createRow(0);
            String[] columns = {"name", "email", "password", "phone", "role", "scopeType", "scopeId", "centerId"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create user template");
        }
    }

    public List<PermissionResponse> permissions() {
        assertUserManager(currentUserService.get());
        return permissionRepository.findAll().stream().map(PermissionResponse::from).toList();
    }

    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        assertPermissionManager(currentUserService.get());
        permissionRepository.findByModuleAndAction(request.module(), request.action()).ifPresent(existing -> {
            throw new AppException(HttpStatus.CONFLICT, "Permission already exists");
        });
        Permission permission = new Permission();
        permission.setModule(request.module());
        permission.setAction(request.action());
        permission.setDescription(request.description());
        return PermissionResponse.from(permissionRepository.save(permission));
    }

    private User createInternal(UserCreateRequest request, User actor) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists: " + request.email());
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setRank(request.role().getRank());
        user.setCenter(request.centerId() != null ? findCenter(request.centerId()) : null);
        user.setScopeType(request.scopeType() != null ? request.scopeType() : defaultScope(request.role()));
        user.setScopeId(request.scopeId() != null ? request.scopeId() : defaultScopeId(user));
        user.setCreatedBy(actor);
        user.setActive(true);
        User saved = userRepository.save(user);
        replacePermissions(saved, request.permissionIds(), actor);
        return saved;
    }

    private void replacePermissions(User user, Set<Long> permissionIds, User grantedBy) {
        if (permissionIds == null) {
            return;
        }
        userPermissionRepository.deleteByUser_Id(user.getId());
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Permission not found: " + permissionId));
            if (grantedBy.getRole() != Role.SUPER_ADMIN
                    && !userPermissionRepository.existsByUser_IdAndPermission_Id(grantedBy.getId(), permissionId)) {
                throw new AppException(HttpStatus.FORBIDDEN, "You can only grant permissions you already possess");
            }
            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setPermission(permission);
            userPermission.setGrantedBy(grantedBy);
            userPermissionRepository.save(userPermission);
        }
    }

    private BulkUserRow parseBulkRow(Row row, int rowNumber) {
        String name = text(row.getCell(0));
        String email = text(row.getCell(1));
        String password = text(row.getCell(2));
        String phone = text(row.getCell(3));
        Role role = parseEnum(Role.class, text(row.getCell(4)));
        ScopeType scopeType = parseEnum(ScopeType.class, text(row.getCell(5)));
        Long scopeId = number(row.getCell(6));
        Long centerId = number(row.getCell(7));
        String reason = validateBulk(name, email, password, role);
        return new BulkUserRow(rowNumber, name, email, phone, role, scopeType, scopeId, centerId,
                reason == null ? "VALID" : "ERROR", reason);
    }

    private String validateBulk(String name, String email, String password, Role role) {
        if (name == null || name.isBlank()) {
            return "Name is required";
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "Valid email is required";
        }
        if (userRepository.existsByEmail(email)) {
            return "Email already exists";
        }
        if (password == null || password.isBlank()) {
            return "Password is required";
        }
        if (role == null) {
            return "Role is invalid";
        }
        return null;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }

    private ScopeType defaultScope(Role role) {
        return switch (role) {
            case SUPER_ADMIN, ADMIN -> ScopeType.SYSTEM;
            case STATE_MANAGER -> ScopeType.STATE;
            case DISTRICT_MANAGER -> ScopeType.DISTRICT;
            case BLOCK_MANAGER -> ScopeType.BLOCK;
            case PATIENT -> ScopeType.SELF;
            default -> ScopeType.CENTER;
        };
    }

    private Long defaultScopeId(User user) {
        return user.getCenter() != null ? user.getCenter().getId() : null;
    }

    private String text(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Long number(Cell cell) {
        String value = text(cell);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private <T extends Enum<T>> T parseEnum(Class<T> type, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void assertUserManager(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN
                && actor.getRole() != Role.ADMIN
                && actor.getRole() != Role.STATE_MANAGER
                && actor.getRole() != Role.DISTRICT_MANAGER
                && actor.getRole() != Role.BLOCK_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot manage users");
        }
    }

    private void assertPermissionManager(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN && actor.getRole() != Role.ADMIN) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only SUPER_ADMIN or ADMIN can create permissions");
        }
    }
}
