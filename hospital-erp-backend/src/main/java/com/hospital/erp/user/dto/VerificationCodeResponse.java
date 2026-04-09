package com.hospital.erp.user.dto;

import java.time.LocalDateTime;

public record VerificationCodeResponse(
        String channel,
        String destination,
        String status,
        String previewCode,
        LocalDateTime expiresAt,
        String message
) {
}
