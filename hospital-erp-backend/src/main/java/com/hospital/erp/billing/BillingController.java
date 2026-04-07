package com.hospital.erp.billing;

import com.hospital.erp.billing.dto.InvoiceRequest;
import com.hospital.erp.billing.dto.PaymentRequest;
import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.common.enums.PaymentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @PostMapping("/billing/invoice")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RECEPTIONIST','PHARMACIST')")
    public ApiResponse<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return ApiResponse.ok(billingService.createInvoice(request), "Invoice created");
    }

    @PostMapping("/billing/invoice/{id}/pay")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RECEPTIONIST')")
    public ApiResponse<Invoice> pay(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.ok(billingService.pay(id, request), "Payment recorded");
    }

    @GetMapping("/billing/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RECEPTIONIST','PHARMACIST','CENTER_STAFF')")
    public ApiResponse<List<Invoice>> invoices(@RequestParam Long centerId, @RequestParam(required = false) PaymentStatus status) {
        return ApiResponse.ok(billingService.invoices(centerId, status), "Invoices loaded");
    }

    @GetMapping("/billing/invoices/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','RECEPTIONIST','PHARMACIST','CENTER_STAFF')")
    public ApiResponse<Invoice> invoice(@PathVariable Long id) {
        return ApiResponse.ok(billingService.findInvoice(id), "Invoice loaded");
    }

    @PostMapping("/billing/invoice/{id}/refund")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<Invoice> refund(@PathVariable Long id) {
        return ApiResponse.ok(billingService.refund(id), "Refund processed");
    }
}
