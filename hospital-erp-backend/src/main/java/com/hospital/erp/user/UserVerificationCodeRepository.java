package com.hospital.erp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationCodeRepository extends JpaRepository<UserVerificationCode, Long> {
    Optional<UserVerificationCode> findTopByUser_IdAndChannelAndUsedAtIsNullOrderByCreatedAtDesc(Long userId, VerificationChannel channel);
}
