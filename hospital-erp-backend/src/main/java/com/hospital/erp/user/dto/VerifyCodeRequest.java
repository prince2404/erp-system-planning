package com.hospital.erp.user.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(@NotBlank String code) {
}
