package com.hospital.erp.user.dto;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;

import java.util.Set;

public record UserUpdateRequest(
        String name,
        String phone,
        Role role,
        Long centerId,
        ScopeType scopeType,
        Long scopeId,
        Boolean active,
        Set<Long> permissionIds
) {
}
