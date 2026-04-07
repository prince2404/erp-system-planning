package com.hospital.erp.user;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.common.PageResponse;
import com.hospital.erp.common.enums.Role;
import com.hospital.erp.user.dto.BulkUserRow;
import com.hospital.erp.user.dto.PermissionRequest;
import com.hospital.erp.user.dto.PermissionResponse;
import com.hospital.erp.user.dto.UserCreateRequest;
import com.hospital.erp.user.dto.UserResponse;
import com.hospital.erp.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<PageResponse<UserResponse>> users(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Long centerId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(userService.list(role, centerId, active, pageable), "Users loaded");
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<UserResponse> user(@PathVariable Long id) {
        return ApiResponse.ok(userService.get(id), "User loaded");
    }

    @PostMapping("/users/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(userService.create(request), "User created");
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ApiResponse.ok(userService.update(id, request), "User updated");
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<UserResponse> deactivate(@PathVariable Long id) {
        return ApiResponse.ok(userService.deactivate(id), "User deactivated");
    }

    @PostMapping(value = "/users/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<List<BulkUserRow>> bulkPreview(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(userService.previewBulk(file), "Bulk preview created");
    }

    @PostMapping("/users/bulk-confirm")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<List<UserResponse>> bulkConfirm(@Valid @RequestBody List<UserCreateRequest> requests) {
        return ApiResponse.ok(userService.bulkConfirm(requests), "Bulk users created");
    }

    @GetMapping("/users/template")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ResponseEntity<ByteArrayResource> template() {
        byte[] content = userService.template();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user-import-template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(content));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<List<PermissionResponse>> permissions() {
        return ApiResponse.ok(userService.permissions(), "Permissions loaded");
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.ok(userService.createPermission(request), "Permission created");
    }
}
