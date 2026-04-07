package com.hospital.erp.user.dto;

import com.hospital.erp.user.Permission;

public record PermissionResponse(Long id, String module, String action, String description) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getModule(),
                permission.getAction(),
                permission.getDescription()
        );
    }
}
