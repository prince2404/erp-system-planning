package com.hospital.erp.user.dto;

import java.util.List;

public record PermissionCatalogGroupResponse(
        String key,
        String label,
        List<PermissionCatalogItemResponse> permissions
) {
}
