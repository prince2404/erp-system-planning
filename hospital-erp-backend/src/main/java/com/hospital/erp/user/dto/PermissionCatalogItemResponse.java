package com.hospital.erp.user.dto;

import java.util.List;

public record PermissionCatalogItemResponse(
        Long id,
        String key,
        String label,
        String description,
        List<String> defaultRoles
) {
}
