package com.hospital.erp.hr.dto;

import com.hospital.erp.common.enums.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveApplyRequest(@NotNull LeaveType leaveType, @NotNull LocalDate fromDate, @NotNull LocalDate toDate, String reason) {
}
