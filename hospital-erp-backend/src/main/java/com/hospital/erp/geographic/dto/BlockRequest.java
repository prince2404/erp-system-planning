package com.hospital.erp.geographic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BlockRequest(@NotBlank String name, @NotNull Long districtId) {
}
