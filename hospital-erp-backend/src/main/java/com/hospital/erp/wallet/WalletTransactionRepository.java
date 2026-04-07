package com.hospital.erp.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWallet_IdOrderByCreatedAtDesc(Long walletId);
    List<WalletTransaction> findByWallet_IdAndCreatedAtBetweenOrderByCreatedAtDesc(Long walletId, LocalDateTime from, LocalDateTime to);
}
