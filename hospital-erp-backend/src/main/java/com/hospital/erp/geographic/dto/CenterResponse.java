package com.hospital.erp.geographic.dto;

import com.hospital.erp.geographic.entities.Center;

public record CenterResponse(
        Long id,
        String code,
        String name,
        String address,
        String city,
        Long blockId,
        String blockName,
        Long districtId,
        String districtName,
        Long stateId,
        String stateName,
        String phone,
        String email,
        String pincode,
        Boolean active
) {
    public static CenterResponse from(Center center) {
        return new CenterResponse(
                center.getId(),
                center.getCode(),
                center.getName(),
                center.getAddress(),
                center.getCity(),
                center.getBlock() != null ? center.getBlock().getId() : null,
                center.getBlock() != null ? center.getBlock().getName() : null,
                center.getDistrict() != null ? center.getDistrict().getId() : null,
                center.getDistrict() != null ? center.getDistrict().getName() : null,
                center.getState() != null ? center.getState().getId() : null,
                center.getState() != null ? center.getState().getName() : null,
                center.getPhone(),
                center.getEmail(),
                center.getPincode(),
                center.getActive()
        );
    }
}
