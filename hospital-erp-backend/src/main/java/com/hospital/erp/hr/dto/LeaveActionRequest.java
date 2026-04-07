package com.hospital.erp.hr.dto;

import com.hospital.erp.common.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;

public record LeaveActionRequest(@NotNull LeaveStatus status) {
}
