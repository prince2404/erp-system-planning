package com.hospital.erp.user.dto;

import com.hospital.erp.user.Permission;
import com.hospital.erp.user.PermissionCatalogService;

public record PermissionResponse(Long id, String key, String module, String action, String description) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                PermissionCatalogService.permissionKey(permission.getModule(), permission.getAction()),
                permission.getModule(),
                permission.getAction(),
                permission.getDescription()
        );
    }
}
