package com.hospital.erp.common.enums;

public enum Role {
    SUPER_ADMIN(1),
    ADMIN(2),
    STATE_MANAGER(3),
    DISTRICT_MANAGER(4),
    BLOCK_MANAGER(5),
    CENTER_MANAGER(6),
    HR_MANAGER(7),
    DOCTOR(8),
    PHARMACIST(9),
    RECEPTIONIST(10),
    ASSOCIATE(11),
    CENTER_STAFF(12),
    PATIENT(13);

    private final int rank;

    Role(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
