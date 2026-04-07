package com.hospital.erp.wallet;

import com.hospital.erp.common.BaseEntity;
import com.hospital.erp.common.enums.WalletEntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallets", uniqueConstraints = @UniqueConstraint(columnNames = {"entity_type", "entity_id"}))
public class Wallet extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WalletEntityType entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
