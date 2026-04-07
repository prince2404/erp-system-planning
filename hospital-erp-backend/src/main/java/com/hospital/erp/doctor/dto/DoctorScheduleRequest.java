package com.hospital.erp.doctor.dto;

import com.hospital.erp.common.enums.DayOfWeekCode;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DoctorScheduleRequest(
        @NotNull DayOfWeekCode dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        Integer slotDurationMins,
        Integer maxPatients,
        Boolean active
) {
}
