package com.hospital.erp.geographic.dto;

import com.hospital.erp.geographic.entities.District;

public record DistrictResponse(Long id, String name, Long stateId, String stateName) {
    public static DistrictResponse from(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getName(),
                district.getState().getId(),
                district.getState().getName()
        );
    }
}
