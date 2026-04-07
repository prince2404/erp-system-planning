package com.hospital.erp.pharmacy.dto;

import jakarta.validation.constraints.NotNull;

public record DispenseRequest(@NotNull Long patientId, Long opdVisitId, @NotNull Long drugId, @NotNull Long centerId, @NotNull Integer quantity) {
}
