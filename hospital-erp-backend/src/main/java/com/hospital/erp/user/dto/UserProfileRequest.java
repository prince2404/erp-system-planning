package com.hospital.erp.user.dto;

import java.time.LocalDate;

public record UserProfileRequest(
        String name,
        String phone,
        String gender,
        LocalDate dateOfBirth,
        String alternatePhone,
        String emergencyContactName,
        String emergencyContactPhone,
        String address,
        String villageOrLocality,
        String pincode,
        String bankAccountName,
        String bankName,
        String bankAccountNumber,
        String ifscCode,
        String upiId,
        String idProofType,
        String idProofNumber
) {
}
