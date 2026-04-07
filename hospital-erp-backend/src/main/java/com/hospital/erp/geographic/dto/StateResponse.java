package com.hospital.erp.geographic.dto;

import com.hospital.erp.geographic.entities.StateEntity;

public record StateResponse(Long id, String name, String code) {
    public static StateResponse from(StateEntity state) {
        return new StateResponse(state.getId(), state.getName(), state.getCode());
    }
}
