package com.hospital.erp.auth;

import com.hospital.erp.auth.dto.AuthRequest;
import com.hospital.erp.auth.dto.AuthResponse;
import com.hospital.erp.auth.dto.BootstrapSuperAdminRequest;
import com.hospital.erp.auth.dto.LogoutRequest;
import com.hospital.erp.auth.dto.TokenRefreshRequest;
import com.hospital.erp.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.ok(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()), "Token refreshed");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.ok(null, "Logged out");
    }

    @PostMapping("/bootstrap-super-admin")
    public ApiResponse<AuthResponse> bootstrapSuperAdmin(@Valid @RequestBody BootstrapSuperAdminRequest request) {
        return ApiResponse.ok(authService.bootstrapSuperAdmin(request), "Super admin created");
    }
}
