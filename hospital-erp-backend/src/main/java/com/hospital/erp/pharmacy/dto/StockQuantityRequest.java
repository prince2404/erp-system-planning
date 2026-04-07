package com.hospital.erp.pharmacy.dto;

import jakarta.validation.constraints.NotNull;

public record StockQuantityRequest(@NotNull Integer quantity) {
}
