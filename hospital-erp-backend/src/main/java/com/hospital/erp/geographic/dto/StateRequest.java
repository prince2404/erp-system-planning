package com.hospital.erp.geographic.dto;

import jakarta.validation.constraints.NotBlank;

public record StateRequest(@NotBlank String name, @NotBlank String code) {
}
