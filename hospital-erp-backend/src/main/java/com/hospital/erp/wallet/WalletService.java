package com.hospital.erp.wallet;

import com.hospital.erp.common.enums.WalletEntityType;
import com.hospital.erp.common.enums.WalletTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public Wallet credit(WalletEntityType entityType, Long entityId, BigDecimal amount, String referenceId, String description) {
        return transact(entityType, entityId, WalletTransactionType.CREDIT, amount, referenceId, description);
    }

    @Transactional
    public Wallet debit(WalletEntityType entityType, Long entityId, BigDecimal amount, String referenceId, String description) {
        return transact(entityType, entityId, WalletTransactionType.DEBIT, amount, referenceId, description);
    }

    public Wallet balance(WalletEntityType entityType, Long entityId) {
        return getOrCreate(entityType, entityId);
    }

    public List<WalletTransaction> transactions(Long walletId, LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null) {
            return walletTransactionRepository.findByWallet_IdAndCreatedAtBetweenOrderByCreatedAtDesc(walletId, from, to);
        }
        return walletTransactionRepository.findByWallet_IdOrderByCreatedAtDesc(walletId);
    }

    private Wallet transact(WalletEntityType entityType, Long entityId, WalletTransactionType type, BigDecimal amount, String referenceId, String description) {
        Wallet wallet = getOrCreate(entityType, entityId);
        wallet.setBalance(type == WalletTransactionType.CREDIT ? wallet.getBalance().add(amount) : wallet.getBalance().subtract(amount));
        wallet.setLastUpdated(LocalDateTime.now());
        Wallet saved = walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(saved);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setReferenceId(referenceId);
        transaction.setDescription(description);
        walletTransactionRepository.save(transaction);
        return saved;
    }

    private Wallet getOrCreate(WalletEntityType entityType, Long entityId) {
        return walletRepository.findByEntityTypeAndEntityId(entityType, entityId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setEntityType(entityType);
            wallet.setEntityId(entityId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setLastUpdated(LocalDateTime.now());
            return walletRepository.save(wallet);
        });
    }
}
