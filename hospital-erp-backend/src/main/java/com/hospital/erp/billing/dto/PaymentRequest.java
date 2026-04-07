package com.hospital.erp.billing.dto;

import com.hospital.erp.common.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(@NotNull PaymentMode paymentMode, String insuranceProvider, String insuranceClaimId) {
}
