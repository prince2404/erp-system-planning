package com.hospital.erp.geographic.dto;

import com.hospital.erp.geographic.entities.Block;

public record BlockResponse(Long id, String name, Long districtId, String districtName) {
    public static BlockResponse from(Block block) {
        return new BlockResponse(
                block.getId(),
                block.getName(),
                block.getDistrict().getId(),
                block.getDistrict().getName()
        );
    }
}
