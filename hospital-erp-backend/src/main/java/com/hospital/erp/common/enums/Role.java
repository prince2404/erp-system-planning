package com.hospital.erp.common.enums;

public enum Role {
    SUPER_ADMIN(1),
    ADMIN(2),
    STATE_MANAGER(3),
    DISTRICT_MANAGER(4),
    BLOCK_MANAGER(5),
    HR_MANAGER(6),
    DOCTOR(7),
    PHARMACIST(8),
    RECEPTIONIST(8),
    CENTER_STAFF(9),
    PATIENT(10);

    private final int rank;

    Role(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
