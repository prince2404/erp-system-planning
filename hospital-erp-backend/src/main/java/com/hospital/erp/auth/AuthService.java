package com.hospital.erp.auth;

import com.hospital.erp.auth.dto.AuthRequest;
import com.hospital.erp.auth.dto.AuthResponse;
import com.hospital.erp.auth.dto.BootstrapSuperAdminRequest;
import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.user.RefreshToken;
import com.hospital.erp.user.RefreshTokenRepository;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserPermissionRepository;
import com.hospital.erp.user.UserRepository;
import com.hospital.erp.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.refresh-expiry-ms}")
    private long refreshExpiryMs;

    @Value("${app.bootstrap-token:}")
    private String bootstrapToken;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return tokensFor(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid"));
        if (Boolean.TRUE.equals(token.getRevoked()) || token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked");
        }
        return tokensFor(token.getUser());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public AuthResponse bootstrapSuperAdmin(BootstrapSuperAdminRequest request) {
        if (userRepository.count() > 0) {
            throw new AppException(HttpStatus.CONFLICT, "Bootstrap is disabled after the first user is created");
        }
        if (bootstrapToken == null || bootstrapToken.isBlank() || !bootstrapToken.equals(request.bootstrapToken())) {
            throw new AppException(HttpStatus.FORBIDDEN, "Bootstrap token is invalid");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.SUPER_ADMIN);
        user.setRank(Role.SUPER_ADMIN.getRank());
        user.setScopeType(ScopeType.SYSTEM);
        user.setActive(true);
        return tokensFor(userRepository.save(user));
    }

    private AuthResponse tokensFor(User user) {
        List<String> permissions = userPermissionRepository.findByUser_Id(user.getId()).stream()
                .map(userPermission -> userPermission.getPermission().getModule() + ":" + userPermission.getPermission().getAction())
                .toList();
        String refreshToken = createRefreshToken(user);
        return new AuthResponse(jwtService.generateAccessToken(user, permissions), refreshToken, UserResponse.from(user), permissions);
    }

    private String createRefreshToken(User user) {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        RefreshToken token = new RefreshToken();
        token.setToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        token.setUser(user);
        token.setExpiry(LocalDateTime.now().plusNanos(refreshExpiryMs * 1_000_000));
        token.setRevoked(false);
        return refreshTokenRepository.save(token).getToken();
    }
}
