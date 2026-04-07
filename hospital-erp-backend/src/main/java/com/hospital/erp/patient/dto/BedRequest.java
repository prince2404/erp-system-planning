package com.hospital.erp.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BedRequest(@NotNull Long centerId, @NotBlank String ward, @NotBlank String bedNumber) {
}
