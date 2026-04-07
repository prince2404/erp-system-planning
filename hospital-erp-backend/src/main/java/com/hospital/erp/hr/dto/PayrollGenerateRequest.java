package com.hospital.erp.hr.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayrollGenerateRequest(
        @NotNull Long centerId,
        @Min(1) @Max(12) int month,
        @NotNull Integer year,
        BigDecimal deductions,
        BigDecimal bonus
) {
}
