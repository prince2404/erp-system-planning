package com.hospital.erp.hr.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StaffProfileRequest(
        @NotNull Long userId,
        String department,
        String designation,
        @NotNull LocalDate dateOfJoining,
        BigDecimal baseSalary,
        String bankAccount,
        String ifscCode,
        String panNumber,
        String aadharNumber,
        String emergencyContact,
        String emergencyName
) {
}
