package com.hospital.erp.user.dto;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role,
        Integer rank,
        Long centerId,
        String centerName,
        ScopeType scopeType,
        Long scopeId,
        Boolean active,
        Boolean emailVerified,
        Boolean phoneVerified,
        Boolean mustChangePassword,
        Boolean profileCompleted,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getRank(),
                user.getCenter() != null ? user.getCenter().getId() : null,
                user.getCenter() != null ? user.getCenter().getName() : null,
                user.getScopeType(),
                user.getScopeId(),
                user.getActive(),
                user.getEmailVerified(),
                user.getPhoneVerified(),
                user.getMustChangePassword(),
                user.getProfileCompleted(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}
