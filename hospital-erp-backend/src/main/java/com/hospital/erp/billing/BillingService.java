package com.hospital.erp.billing;

import com.hospital.erp.billing.dto.InvoiceItemRequest;
import com.hospital.erp.billing.dto.InvoiceRequest;
import com.hospital.erp.billing.dto.PaymentRequest;
import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.CommissionStatus;
import com.hospital.erp.common.enums.InvoiceItemType;
import com.hospital.erp.common.enums.InvoiceType;
import com.hospital.erp.common.enums.NotificationType;
import com.hospital.erp.common.enums.PaymentStatus;
import com.hospital.erp.common.enums.WalletEntityType;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.notification.NotificationService;
import com.hospital.erp.patient.IpdAdmission;
import com.hospital.erp.patient.Patient;
import com.hospital.erp.patient.PatientRepository;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.User;
import com.hospital.erp.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {
    private static final BigDecimal GST_PHARMACY = BigDecimal.valueOf(0.18);
    private static final BigDecimal COMMISSION_RATE = BigDecimal.valueOf(10);

    private final InvoiceRepository invoiceRepository;
    private final CommissionRepository commissionRepository;
    private final PatientRepository patientRepository;
    private final CenterRepository centerRepository;
    private final CurrentUserService currentUserService;
    private final WalletService walletService;
    private final NotificationService notificationService;

    @Transactional
    public Invoice createInvoice(InvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextInvoiceNumber());
        invoice.setPatient(findPatient(request.patientId()));
        invoice.setCenter(findCenter(request.centerId()));
        invoice.setType(request.type());
        invoice.setDiscount(request.discount() != null ? request.discount() : BigDecimal.ZERO);
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        invoice.setCreatedBy(currentUserService.get());
        request.items().forEach(item -> addItem(invoice, item));
        recalculate(invoice);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice createIpdDraft(IpdAdmission admission) {
        long days = Math.max(1, Duration.between(admission.getAdmissionDate(), admission.getDischargeDate()).toDays());
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextInvoiceNumber());
        invoice.setPatient(admission.getPatient());
        invoice.setCenter(admission.getCenter());
        invoice.setType(InvoiceType.IPD);
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setPaymentStatus(PaymentStatus.PENDING);
        invoice.setCreatedBy(admission.getDischargedBy());
        addItem(invoice, new InvoiceItemRequest("IPD bed charges (" + days + " day/s)", InvoiceItemType.BED_CHARGE, (int) days, admission.getDailyCharge()));
        recalculate(invoice);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice pay(Long id, PaymentRequest request) {
        Invoice invoice = findInvoice(id);
        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new AppException(HttpStatus.CONFLICT, "Invoice is already paid");
        }
        invoice.setPaymentMode(request.paymentMode());
        invoice.setInsuranceProvider(request.insuranceProvider());
        invoice.setInsuranceClaimId(request.insuranceClaimId());
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        Invoice paid = invoiceRepository.save(invoice);

        walletService.credit(WalletEntityType.CENTER, paid.getCenter().getId(), paid.getTotalAmount(), paid.getInvoiceNumber(), "Invoice payment received");
        createCommission(paid);
        notificationService.notifyCenter(
                paid.getCenter().getId(),
                NotificationType.INVOICE_PAID,
                "Invoice paid",
                paid.getInvoiceNumber() + " paid for " + paid.getTotalAmount(),
                paid.getId(),
                "INVOICE"
        );
        return paid;
    }

    @Transactional
    public Invoice refund(Long id) {
        Invoice invoice = findInvoice(id);
        if (invoice.getPaymentStatus() != PaymentStatus.PAID) {
            throw new AppException(HttpStatus.CONFLICT, "Only paid invoices can be refunded");
        }
        invoice.setPaymentStatus(PaymentStatus.REFUNDED);
        walletService.debit(WalletEntityType.CENTER, invoice.getCenter().getId(), invoice.getTotalAmount(), invoice.getInvoiceNumber(), "Invoice refund processed");
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> invoices(Long centerId, PaymentStatus status) {
        if (status != null) {
            return invoiceRepository.findByCenter_IdAndPaymentStatus(centerId, status);
        }
        return invoiceRepository.findByCenter_Id(centerId);
    }

    public Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private void addItem(Invoice invoice, InvoiceItemRequest item) {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setDescription(item.description());
        invoiceItem.setItemType(item.itemType());
        invoiceItem.setQuantity(item.quantity() != null ? item.quantity() : 1);
        invoiceItem.setUnitPrice(item.unitPrice() != null ? item.unitPrice() : BigDecimal.ZERO);
        invoiceItem.setTotalPrice(invoiceItem.getUnitPrice().multiply(BigDecimal.valueOf(invoiceItem.getQuantity())));
        invoice.getItems().add(invoiceItem);
    }

    private void recalculate(Invoice invoice) {
        BigDecimal subtotal = invoice.getItems().stream().map(InvoiceItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = invoice.getItems().stream()
                .filter(item -> item.getItemType() == InvoiceItemType.MEDICINE)
                .map(item -> item.getTotalPrice().multiply(GST_PHARMACY))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(subtotal.add(tax).subtract(invoice.getDiscount() != null ? invoice.getDiscount() : BigDecimal.ZERO));
    }

    private void createCommission(Invoice invoice) {
        User agent = invoice.getCreatedBy();
        Commission commission = new Commission();
        commission.setInvoice(invoice);
        commission.setAgentUser(agent);
        commission.setRate(COMMISSION_RATE);
        commission.setAmount(invoice.getTotalAmount().multiply(COMMISSION_RATE).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        commission.setStatus(CommissionStatus.PENDING);
        commissionRepository.save(commission);
        if (agent != null) {
            notificationService.create(agent, NotificationType.COMMISSION_EARNED, "Commission earned", "Commission recorded for " + invoice.getInvoiceNumber(), invoice.getId(), "INVOICE");
        }
    }

    private String nextInvoiceNumber() {
        String prefix = "INV-" + LocalDate.now().getYear() + "-";
        long next = invoiceRepository.countByInvoiceNumberStartingWith(prefix) + 1;
        return prefix + String.format("%06d", next);
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }
}
