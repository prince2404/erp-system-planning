package com.hospital.erp.geographic.dto;

import com.hospital.erp.geographic.entities.Center;

public record CenterResponse(
        Long id,
        String name,
        String address,
        String city,
        Long blockId,
        Long districtId,
        Long stateId,
        String phone,
        String email,
        Boolean active
) {
    public static CenterResponse from(Center center) {
        return new CenterResponse(
                center.getId(),
                center.getName(),
                center.getAddress(),
                center.getCity(),
                center.getBlock() != null ? center.getBlock().getId() : null,
                center.getDistrict() != null ? center.getDistrict().getId() : null,
                center.getState() != null ? center.getState().getId() : null,
                center.getPhone(),
                center.getEmail(),
                center.getActive()
        );
    }
}
