package com.hospital.erp.auth.dto;

import com.hospital.erp.user.dto.UserResponse;

import java.util.List;

public record AuthResponse(String accessToken, String refreshToken, UserResponse user, List<String> permissions) {
}
