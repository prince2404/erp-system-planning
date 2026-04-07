package com.hospital.erp.doctor.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DoctorProfileRequest(
        @NotNull Long userId,
        String specialization,
        String qualification,
        Integer experienceYears,
        BigDecimal consultationFee,
        Long centerId,
        Boolean available
) {
}
