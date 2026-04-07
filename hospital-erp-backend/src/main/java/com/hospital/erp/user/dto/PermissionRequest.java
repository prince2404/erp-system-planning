package com.hospital.erp.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionRequest(@NotBlank String module, @NotBlank String action, String description) {
}
