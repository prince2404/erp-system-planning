package com.hospital.erp.billing.dto;

import com.hospital.erp.common.enums.InvoiceItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceItemRequest(@NotBlank String description, @NotNull InvoiceItemType itemType, Integer quantity, @NotNull BigDecimal unitPrice) {
}
