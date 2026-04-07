package com.hospital.erp.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DrugStockRequest(
        @NotNull Long drugId,
        @NotNull Long centerId,
        @NotBlank String batchNumber,
        @NotNull LocalDate expiryDate,
        @NotNull Integer quantity,
        BigDecimal purchasePrice,
        BigDecimal sellingPrice,
        String supplier,
        LocalDate receivedDate
) {
}
