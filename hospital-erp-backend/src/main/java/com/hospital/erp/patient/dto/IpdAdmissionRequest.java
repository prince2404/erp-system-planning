package com.hospital.erp.patient.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IpdAdmissionRequest(
        @NotNull Long patientId,
        Long doctorId,
        @NotNull Long centerId,
        @NotNull Long bedId,
        String diagnosis,
        String treatmentNotes,
        BigDecimal dailyCharge
) {
}
