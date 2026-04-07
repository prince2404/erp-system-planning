package com.hospital.erp.patient.dto;

import com.hospital.erp.common.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;

public record OpdStatusRequest(@NotNull VisitStatus status, String diagnosis, String prescriptionNotes) {
}
