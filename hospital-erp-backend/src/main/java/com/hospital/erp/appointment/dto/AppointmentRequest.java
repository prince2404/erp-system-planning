package com.hospital.erp.appointment.dto;

import com.hospital.erp.common.enums.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        Long centerId,
        @NotNull LocalDate appointmentDate,
        @NotNull LocalTime slotTime,
        AppointmentType type,
        BigDecimal bookingFee,
        String notes
) {
}
