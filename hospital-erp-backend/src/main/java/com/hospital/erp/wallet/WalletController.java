package com.hospital.erp.wallet;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.common.enums.WalletEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/wallet/balance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<Wallet> balance(@RequestParam WalletEntityType entityType, @RequestParam Long entityId) {
        return ApiResponse.ok(walletService.balance(entityType, entityId), "Wallet balance loaded");
    }

    @GetMapping("/wallet/transactions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','STATE_MANAGER','DISTRICT_MANAGER','BLOCK_MANAGER')")
    public ApiResponse<List<WalletTransaction>> transactions(
            @RequestParam Long walletId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return ApiResponse.ok(walletService.transactions(walletId, from, to), "Wallet transactions loaded");
    }
}
