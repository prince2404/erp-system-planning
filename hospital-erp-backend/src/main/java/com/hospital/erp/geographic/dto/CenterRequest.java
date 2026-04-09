package com.hospital.erp.geographic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CenterRequest(
        @NotBlank String name,
        String address,
        String city,
        @NotNull Long blockId,
        @NotNull Long districtId,
        @NotNull Long stateId,
        String phone,
        String email,
        String pincode
) {
}
