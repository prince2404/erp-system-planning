package com.hospital.erp.patient.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpdVisitRequest(
        @NotNull Long patientId,
        Long doctorId,
        @NotNull Long centerId,
        LocalDate visitDate,
        String symptoms,
        BigDecimal fee
) {
}
