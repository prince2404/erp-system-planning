package com.hospital.erp.appointment.dto;

import com.hospital.erp.common.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusRequest(@NotNull AppointmentStatus status) {
}
