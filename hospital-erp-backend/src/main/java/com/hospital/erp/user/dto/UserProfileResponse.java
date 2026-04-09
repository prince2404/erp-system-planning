package com.hospital.erp.user.dto;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserProfile;

import java.time.LocalDate;

public record UserProfileResponse(
        Long userId,
        String name,
        String email,
        String phone,
        Role role,
        String centerName,
        Boolean emailVerified,
        Boolean phoneVerified,
        Boolean mustChangePassword,
        Boolean profileCompleted,
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
    public static UserProfileResponse from(User user, UserProfile profile) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCenter() != null ? user.getCenter().getName() : null,
                user.getEmailVerified(),
                user.getPhoneVerified(),
                user.getMustChangePassword(),
                user.getProfileCompleted(),
                profile != null ? profile.getGender() : null,
                profile != null ? profile.getDateOfBirth() : null,
                profile != null ? profile.getAlternatePhone() : null,
                profile != null ? profile.getEmergencyContactName() : null,
                profile != null ? profile.getEmergencyContactPhone() : null,
                profile != null ? profile.getAddress() : null,
                profile != null ? profile.getVillageOrLocality() : null,
                profile != null ? profile.getPincode() : null,
                profile != null ? profile.getBankAccountName() : null,
                profile != null ? profile.getBankName() : null,
                profile != null ? profile.getBankAccountNumber() : null,
                profile != null ? profile.getIfscCode() : null,
                profile != null ? profile.getUpiId() : null,
                profile != null ? profile.getIdProofType() : null,
                profile != null ? profile.getIdProofNumber() : null
        );
    }
}
