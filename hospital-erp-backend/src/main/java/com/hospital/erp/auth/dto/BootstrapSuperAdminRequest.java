package com.hospital.erp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BootstrapSuperAdminRequest(
        @NotBlank String bootstrapToken,
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        String phone
) {
}
