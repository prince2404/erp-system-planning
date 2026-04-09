package com.hospital.erp.user.dto;

import java.util.List;
import java.util.Map;

public record UserManagementOptionsResponse(
        List<String> manageableRoles,
        Map<String, String> defaultScopeByRole,
        Map<String, List<String>> defaultPermissionKeysByRole,
        List<PermissionCatalogGroupResponse> permissionGroups
) {
}
