package com.hospital.erp.user;

import com.hospital.erp.common.AppException;
import com.hospital.erp.user.dto.ChangePasswordRequest;
import com.hospital.erp.user.dto.UserProfileRequest;
import com.hospital.erp.user.dto.UserProfileResponse;
import com.hospital.erp.user.dto.VerificationCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserVerificationCodeRepository userVerificationCodeRepository;
    private final UserNotificationService userNotificationService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse myProfile() {
        User user = currentUserService.get();
        return UserProfileResponse.from(user, ensureProfile(user));
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UserProfileRequest request) {
        User user = currentUserService.get();
        UserProfile profile = ensureProfile(user);

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        if (request.phone() != null && !request.phone().isBlank() && !request.phone().equals(user.getPhone())) {
            user.setPhone(request.phone().trim());
            user.setPhoneVerified(false);
        }

        profile.setGender(blankToNull(request.gender()));
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setAlternatePhone(blankToNull(request.alternatePhone()));
        profile.setEmergencyContactName(blankToNull(request.emergencyContactName()));
        profile.setEmergencyContactPhone(blankToNull(request.emergencyContactPhone()));
        profile.setAddress(blankToNull(request.address()));
        profile.setVillageOrLocality(blankToNull(request.villageOrLocality()));
        profile.setPincode(blankToNull(request.pincode()));
        profile.setBankAccountName(blankToNull(request.bankAccountName()));
        profile.setBankName(blankToNull(request.bankName()));
        profile.setBankAccountNumber(blankToNull(request.bankAccountNumber()));
        profile.setIfscCode(blankToNull(request.ifscCode()));
        profile.setUpiId(blankToNull(request.upiId()));
        profile.setIdProofType(blankToNull(request.idProofType()));
        profile.setIdProofNumber(blankToNull(request.idProofNumber()));

        user.setProfileCompleted(isProfileCompleted(user, profile));
        userRepository.save(user);
        userProfileRepository.save(profile);
        return UserProfileResponse.from(user, profile);
    }

    @Transactional
    public UserProfileResponse changePassword(ChangePasswordRequest request) {
        User user = currentUserService.get();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (request.newPassword().length() < 8) {
            throw new AppException(HttpStatus.BAD_REQUEST, "New password must be at least 8 characters");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        return UserProfileResponse.from(user, ensureProfile(user));
    }

    @Transactional
    public VerificationCodeResponse requestEmailVerification() {
        User user = currentUserService.get();
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            return new VerificationCodeResponse("EMAIL", user.getEmail(), "ALREADY_VERIFIED", null, null, "Email is already verified.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email is not available for this user");
        }
        return createAndSendCode(user, VerificationChannel.EMAIL, user.getEmail());
    }

    @Transactional
    public VerificationCodeResponse requestPhoneVerification() {
        User user = currentUserService.get();
        if (Boolean.TRUE.equals(user.getPhoneVerified())) {
            return new VerificationCodeResponse("PHONE", user.getPhone(), "ALREADY_VERIFIED", null, null, "Phone is already verified.");
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Phone is not available for this user");
        }
        return createAndSendCode(user, VerificationChannel.PHONE, user.getPhone());
    }

    @Transactional
    public UserProfileResponse verifyEmail(String code) {
        return verify(currentUserService.get(), VerificationChannel.EMAIL, code, user -> user.setEmailVerified(true));
    }

    @Transactional
    public UserProfileResponse verifyPhone(String code) {
        return verify(currentUserService.get(), VerificationChannel.PHONE, code, user -> user.setPhoneVerified(true));
    }

    private VerificationCodeResponse createAndSendCode(User user, VerificationChannel channel, String destination) {
        UserVerificationCode verificationCode = new UserVerificationCode();
        verificationCode.setUser(user);
        verificationCode.setChannel(channel);
        verificationCode.setTargetValue(destination);
        verificationCode.setCode(generateCode());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        userVerificationCodeRepository.save(verificationCode);
        return userNotificationService.sendVerificationCode(user, channel, destination, verificationCode.getCode(), verificationCode.getExpiresAt());
    }

    private UserProfileResponse verify(User user, VerificationChannel channel, String code, Consumer<User> verifier) {
        UserVerificationCode latestCode = userVerificationCodeRepository
                .findTopByUser_IdAndChannelAndUsedAtIsNullOrderByCreatedAtDesc(user.getId(), channel)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "No verification code has been requested"));
        if (latestCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Verification code has expired");
        }
        if (!latestCode.getCode().equals(code)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Verification code is invalid");
        }
        latestCode.setUsedAt(LocalDateTime.now());
        userVerificationCodeRepository.save(latestCode);
        verifier.accept(user);
        user.setProfileCompleted(isProfileCompleted(user, ensureProfile(user)));
        userRepository.save(user);
        return UserProfileResponse.from(user, ensureProfile(user));
    }

    private UserProfile ensureProfile(User user) {
        return userProfileRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            return userProfileRepository.save(profile);
        });
    }

    private boolean isProfileCompleted(User user, UserProfile profile) {
        return user.getName() != null && !user.getName().isBlank()
                && user.getPhone() != null && !user.getPhone().isBlank()
                && profile.getAddress() != null && !profile.getAddress().isBlank()
                && profile.getBankName() != null && !profile.getBankName().isBlank()
                && profile.getBankAccountNumber() != null && !profile.getBankAccountNumber().isBlank()
                && profile.getIfscCode() != null && !profile.getIfscCode().isBlank();
    }

    private String generateCode() {
        int code = new SecureRandom().nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
