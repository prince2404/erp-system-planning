package com.hospital.erp.user.dto;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;

public record BulkUserRow(
        int rowNumber,
        String name,
        String email,
        String phone,
        Role role,
        ScopeType scopeType,
        Long scopeId,
        Long centerId,
        String status,
        String reason
) {
}
