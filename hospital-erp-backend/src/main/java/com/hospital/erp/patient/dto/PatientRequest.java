package com.hospital.erp.patient.dto;

import com.hospital.erp.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String name,
        Integer age,
        LocalDate dateOfBirth,
        Gender gender,
        String bloodGroup,
        @NotBlank String phone,
        String email,
        String address,
        String emergencyContact,
        String emergencyName,
        String allergies,
        @NotNull Long centerId
) {
}
