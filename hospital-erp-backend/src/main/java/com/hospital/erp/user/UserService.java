package com.hospital.erp.user;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.PageResponse;
import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.geographic.entities.Block;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.entities.District;
import com.hospital.erp.geographic.entities.StateEntity;
import com.hospital.erp.geographic.repositories.BlockRepository;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.geographic.repositories.DistrictRepository;
import com.hospital.erp.geographic.repositories.StateRepository;
import com.hospital.erp.user.dto.BulkUserRow;
import com.hospital.erp.user.dto.NotificationDeliveryResponse;
import com.hospital.erp.user.dto.PermissionRequest;
import com.hospital.erp.user.dto.PermissionResponse;
import com.hospital.erp.user.dto.UserCreateRequest;
import com.hospital.erp.user.dto.UserManagementOptionsResponse;
import com.hospital.erp.user.dto.UserProvisionResponse;
import com.hospital.erp.user.dto.UserResponse;
import com.hospital.erp.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final CenterRepository centerRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final RankGuard rankGuard;
    private final PermissionCatalogService permissionCatalogService;
    private final UserNotificationService userNotificationService;

    public UserManagementOptionsResponse options() {
        User actor = currentUserService.get();
        assertUserManager(actor);
        return permissionCatalogService.managementOptions(actor);
    }

    public PageResponse<UserResponse> list(Role role, Long centerId, Boolean active, Pageable pageable) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        List<UserResponse> visible = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> centerId == null || (user.getCenter() != null && centerId.equals(user.getCenter().getId())))
                .filter(user -> active == null || active.equals(user.getActive()))
                .filter(user -> canViewUserInScope(actor, user))
                .map(UserResponse::from)
                .toList();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int fromIndex = Math.min(page * size, visible.size());
        int toIndex = Math.min(fromIndex + size, visible.size());
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) visible.size() / size);

        return new PageResponse<>(
                visible.subList(fromIndex, toIndex),
                page,
                size,
                visible.size(),
                totalPages,
                toIndex >= visible.size()
        );
    }

    public UserResponse get(Long id) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        User user = findUser(id);
        if (!canViewUserInScope(actor, user)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You cannot view this user");
        }
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        rankGuard.assertCanCreate(actor, request.role());
        return UserResponse.from(createInternal(request, actor));
    }

    @Transactional
    public UserProvisionResponse provision(UserCreateRequest request) {
        User actor = currentUserService.get();
        assertUserManager(actor);
        rankGuard.assertCanCreate(actor, request.role());

        ScopeSelection scopeSelection = resolveScopeSelection(request.role(), request.stateId(), request.districtId(), request.blockId(), request.centerId(), request.scopeType(), request.scopeId());
        String temporaryPassword = request.password() != null && !request.password().isBlank()
                ? request.password()
                : generateTemporaryPassword();
        Set<Long> permissionIds = resolvePermissionIds(request.role(), request.permissionIds(), request.permissionKeys());

        UserCreateRequest normalized = new UserCreateRequest(
                request.name(),
                request.email(),
                temporaryPassword,
                request.phone(),
                request.role(),
                request.stateId(),
                request.districtId(),
                request.blockId(),
                scopeSelection.centerId(),
                scopeSelection.scopeType(),
                scopeSelection.scopeId(),
                request.notifyByEmail(),
                request.notifyBySms(),
                permissionIds,
                request.permissionKeys()
        );

        User saved = createInternal(normalized, actor);
        boolean sendEmail = request.notifyByEmail() == null || request.notifyByEmail();
        boolean sendSms = request.notifyBySms() != null && request.notifyBySms();
        List<NotificationDeliveryResponse> notifications = userNotificationService.sendWelcome(saved, temporaryPassword, sendEmail, sendSms);
        List<String> permissionKeys = userPermissionRepository.findByUser_Id(saved.getId()).stream()
                .map(userPermission -> PermissionCatalogService.permissionKey(userPermission.getPermission().getModule(), userPermission.getPermission().getAction()))
                .toList();

        return new UserProvisionResponse(UserResponse.from(saved), temporaryPassword, permissionKeys, notifications);
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
        replacePermissions(saved, resolvePermissionIds(saved.getRole(), request.permissionIds(), request.permissionKeys()), actor);
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
            String[] columns = {"name", "email", "phone", "role", "stateId", "districtId", "blockId", "centerId", "notifyByEmail", "notifyBySms"};
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
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        user.setRole(request.role());
        user.setRank(request.role().getRank());
        user.setCenter(request.centerId() != null ? findCenter(request.centerId()) : null);
        user.setScopeType(request.scopeType() != null ? request.scopeType() : defaultScope(request.role()));
        user.setScopeId(request.scopeId() != null ? request.scopeId() : defaultScopeId(user));
        user.setCreatedBy(actor);
        user.setActive(true);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setMustChangePassword(true);
        user.setProfileCompleted(false);
        User saved = userRepository.save(user);
        ensureProfile(saved);
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
            String permissionKey = PermissionCatalogService.permissionKey(permission.getModule(), permission.getAction());
            if (!permissionCatalogService.canGrant(grantedBy, permissionKey)) {
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
        String phone = text(row.getCell(2));
        Role role = parseEnum(Role.class, text(row.getCell(3)));
        Long stateId = number(row.getCell(4));
        Long districtId = number(row.getCell(5));
        Long blockId = number(row.getCell(6));
        Long centerId = number(row.getCell(7));
        ScopeType scopeType = role != null ? permissionCatalogService.defaultScope(role) : null;
        Long scopeId = centerId != null ? centerId : (blockId != null ? blockId : (districtId != null ? districtId : stateId));
        String reason = validateBulk(name, email, role);
        return new BulkUserRow(rowNumber, name, email, phone, role, scopeType, scopeId, centerId,
                reason == null ? "VALID" : "ERROR", reason);
    }

    private String validateBulk(String name, String email, Role role) {
        if (name == null || name.isBlank()) {
            return "Name is required";
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "Valid email is required";
        }
        if (userRepository.existsByEmail(email)) {
            return "Email already exists";
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

    private StateEntity findState(Long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "State not found"));
    }

    private District findDistrict(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "District not found"));
    }

    private Block findBlock(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Block not found"));
    }

    private ScopeType defaultScope(Role role) {
        return switch (role) {
            case SUPER_ADMIN, ADMIN -> ScopeType.SYSTEM;
            case STATE_MANAGER -> ScopeType.STATE;
            case DISTRICT_MANAGER -> ScopeType.DISTRICT;
            case BLOCK_MANAGER, ASSOCIATE -> ScopeType.BLOCK;
            case PATIENT -> ScopeType.SELF;
            default -> ScopeType.CENTER;
        };
    }

    private Long defaultScopeId(User user) {
        return user.getCenter() != null ? user.getCenter().getId() : null;
    }

    private Set<Long> resolvePermissionIds(Role role, Set<Long> permissionIds, Set<String> permissionKeys) {
        Set<Long> ids = new LinkedHashSet<>();
        if (permissionIds != null) {
            ids.addAll(permissionIds);
        }

        Set<String> keys = new LinkedHashSet<>();
        if (permissionKeys != null) {
            keys.addAll(permissionKeys);
        }

        if (!keys.isEmpty()) {
            for (String key : keys) {
                String[] parts = key.split("\\.", 2);
                if (parts.length != 2) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Invalid permission key: " + key);
                }
                Permission permission = permissionRepository.findByModuleAndAction(parts[0].toUpperCase(), parts[1].toUpperCase())
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Permission not found: " + key));
                ids.add(permission.getId());
            }
        }

        if (ids.isEmpty()) {
            for (String key : permissionCatalogService.defaultPermissionKeys(role)) {
                String[] parts = key.split("\\.", 2);
                Permission permission = permissionRepository.findByModuleAndAction(parts[0].toUpperCase(), parts[1].toUpperCase())
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Permission not found: " + key));
                ids.add(permission.getId());
            }
        }

        return ids;
    }

    private ScopeSelection resolveScopeSelection(Role role, Long stateId, Long districtId, Long blockId, Long centerId, ScopeType scopeType, Long scopeId) {
        StateEntity state = stateId != null ? findState(stateId) : null;
        District district = districtId != null ? findDistrict(districtId) : null;
        Block block = blockId != null ? findBlock(blockId) : null;
        Center center = centerId != null ? findCenter(centerId) : null;

        if (district != null && state != null && !district.getState().getId().equals(state.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "District does not belong to the selected state");
        }
        if (block != null && district != null && !block.getDistrict().getId().equals(district.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Block does not belong to the selected district");
        }
        if (center != null && block != null && (center.getBlock() == null || !center.getBlock().getId().equals(block.getId()))) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Center does not belong to the selected block");
        }
        if (center != null && district != null && (center.getDistrict() == null || !center.getDistrict().getId().equals(district.getId()))) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Center does not belong to the selected district");
        }
        if (center != null && state != null && (center.getState() == null || !center.getState().getId().equals(state.getId()))) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Center does not belong to the selected state");
        }

        if (scopeType != null) {
            return new ScopeSelection(centerId, scopeType, scopeId);
        }

        return switch (defaultScope(role)) {
            case SYSTEM -> new ScopeSelection(null, ScopeType.SYSTEM, null);
            case STATE -> new ScopeSelection(null, ScopeType.STATE, requiredValue(stateId, "State is required for this role"));
            case DISTRICT -> new ScopeSelection(null, ScopeType.DISTRICT, requiredValue(districtId, "District is required for this role"));
            case BLOCK -> {
                if (role == Role.ASSOCIATE && blockId == null && center != null) {
                    yield new ScopeSelection(centerId, ScopeType.CENTER, centerId);
                }
                yield new ScopeSelection(centerId, ScopeType.BLOCK, requiredValue(blockId, "Block is required for this role"));
            }
            case CENTER -> new ScopeSelection(requiredValue(centerId, "Center is required for this role"), ScopeType.CENTER, centerId);
            case SELF -> new ScopeSelection(null, ScopeType.SELF, null);
        };
    }

    private Long requiredValue(Long value, String message) {
        if (value == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, message);
        }
        return value;
    }

    private boolean canViewUserInScope(User actor, User target) {
        if (actor.getRole() == Role.SUPER_ADMIN || actor.getRole() == Role.ADMIN || actor.getScopeType() == ScopeType.SYSTEM) {
            return true;
        }
        if (actor.getScopeId() != null && actor.getScopeType() == target.getScopeType() && actor.getScopeId().equals(target.getScopeId())) {
            return true;
        }
        if (target.getCenter() == null || actor.getScopeId() == null) {
            return false;
        }
        return switch (actor.getScopeType()) {
            case STATE -> target.getCenter().getState() != null && actor.getScopeId().equals(target.getCenter().getState().getId());
            case DISTRICT -> target.getCenter().getDistrict() != null && actor.getScopeId().equals(target.getCenter().getDistrict().getId());
            case BLOCK -> target.getCenter().getBlock() != null && actor.getScopeId().equals(target.getCenter().getBlock().getId());
            case CENTER -> actor.getScopeId().equals(target.getCenter().getId());
            default -> false;
        };
    }

    private void ensureProfile(User user) {
        userProfileRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            return userProfileRepository.save(profile);
        });
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
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
                && actor.getRole() != Role.BLOCK_MANAGER
                && actor.getRole() != Role.CENTER_MANAGER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Your role cannot manage users");
        }
    }

    private void assertPermissionManager(User actor) {
        if (actor.getRole() != Role.SUPER_ADMIN && actor.getRole() != Role.ADMIN) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only SUPER_ADMIN or ADMIN can create permissions");
        }
    }

    private record ScopeSelection(Long centerId, ScopeType scopeType, Long scopeId) {
    }
}
