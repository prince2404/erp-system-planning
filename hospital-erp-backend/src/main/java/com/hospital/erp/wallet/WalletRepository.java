package com.hospital.erp.wallet;

import com.hospital.erp.common.enums.WalletEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByEntityTypeAndEntityId(WalletEntityType entityType, Long entityId);
}
