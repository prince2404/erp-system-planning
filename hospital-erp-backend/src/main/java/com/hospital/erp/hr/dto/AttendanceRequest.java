package com.hospital.erp.hr.dto;

import com.hospital.erp.common.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceRequest(
        @NotNull Long userId,
        @NotNull Long centerId,
        @NotNull LocalDate date,
        LocalTime checkIn,
        LocalTime checkOut,
        @NotNull AttendanceStatus status,
        String remarks
) {
}
