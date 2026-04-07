package com.hospital.erp.pharmacy.dto;

import com.hospital.erp.common.enums.DrugUnit;
import jakarta.validation.constraints.NotBlank;

public record DrugRequest(
        @NotBlank String name,
        String genericName,
        String category,
        DrugUnit unit,
        String hsnCode,
        String manufacturer
) {
}
