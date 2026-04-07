package com.hospital.erp.user.dto;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UserCreateRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        String phone,
        @NotNull Role role,
        Long centerId,
        ScopeType scopeType,
        Long scopeId,
        Set<Long> permissionIds
) {
}
