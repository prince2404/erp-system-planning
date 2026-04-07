package com.hospital.erp.billing.dto;

import com.hospital.erp.common.enums.InvoiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceRequest(
        @NotNull Long patientId,
        @NotNull Long centerId,
        @NotNull InvoiceType type,
        BigDecimal discount,
        @Valid @NotEmpty List<InvoiceItemRequest> items
) {
}
