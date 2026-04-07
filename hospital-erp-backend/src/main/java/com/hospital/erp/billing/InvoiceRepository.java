package com.hospital.erp.billing;

import com.hospital.erp.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCenter_Id(Long centerId);
    List<Invoice> findByCenter_IdAndPaymentStatus(Long centerId, PaymentStatus status);
    long countByInvoiceNumberStartingWith(String prefix);
    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
