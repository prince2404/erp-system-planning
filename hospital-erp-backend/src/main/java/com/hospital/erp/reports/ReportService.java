package com.hospital.erp.reports;

import com.hospital.erp.billing.Invoice;
import com.hospital.erp.billing.InvoiceRepository;
import com.hospital.erp.common.enums.PaymentStatus;
import com.hospital.erp.common.enums.StockAlertType;
import com.hospital.erp.hr.AttendanceRepository;
import com.hospital.erp.hr.LeaveRequestRepository;
import com.hospital.erp.patient.OpdVisitRepository;
import com.hospital.erp.patient.PatientRepository;
import com.hospital.erp.pharmacy.DrugStock;
import com.hospital.erp.pharmacy.DrugStockRepository;
import com.hospital.erp.pharmacy.StockAlertRepository;
import com.hospital.erp.wallet.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PatientRepository patientRepository;
    private final OpdVisitRepository opdVisitRepository;
    private final InvoiceRepository invoiceRepository;
    private final DrugStockRepository drugStockRepository;
    private final StockAlertRepository stockAlertRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public Map<String, Object> dashboard(Long centerId, LocalDate from, LocalDate to) {
        List<Invoice> invoices = centerId != null ? invoiceRepository.findByCenter_Id(centerId) : invoiceRepository.findAll();
        BigDecimal paidRevenue = invoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "totalPatients", patientRepository.count(),
                "todayOpdWaiting", centerId != null ? opdVisitRepository.countByCenter_IdAndVisitDateAndStatus(centerId, LocalDate.now(), com.hospital.erp.common.enums.VisitStatus.WAITING) : 0,
                "paidRevenue", paidRevenue,
                "invoiceCount", invoices.size(),
                "activeStockAlerts", centerId != null ? stockAlertRepository.findByCenter_IdAndResolvedFalse(centerId).size() : 0
        );
    }

    public List<Map<String, Object>> revenue(Long centerId, String groupBy) {
        List<Invoice> invoices = centerId != null ? invoiceRepository.findByCenter_Id(centerId) : invoiceRepository.findAll();
        return invoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .collect(Collectors.groupingBy(invoice -> groupKey(invoice, groupBy), Collectors.reducing(BigDecimal.ZERO, Invoice::getTotalAmount, BigDecimal::add)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> Map.<String, Object>of("period", entry.getKey(), "revenue", entry.getValue()))
                .toList();
    }

    public Map<String, Object> patients(Long centerId, LocalDate from, LocalDate to) {
        return Map.of(
                "totalPatients", patientRepository.count(),
                "from", from,
                "to", to,
                "centerId", centerId
        );
    }

    public Map<String, Object> inventory(Long centerId) {
        List<DrugStock> stocks = drugStockRepository.findByCenter_Id(centerId);
        BigDecimal stockValue = stocks.stream()
                .map(stock -> (stock.getSellingPrice() != null ? stock.getSellingPrice() : BigDecimal.ZERO).multiply(BigDecimal.valueOf(stock.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<StockAlertType, Long> alerts = stockAlertRepository.findByCenter_IdAndResolvedFalse(centerId).stream()
                .collect(Collectors.groupingBy(alert -> alert.getAlertType(), Collectors.counting()));
        return Map.of("stockValue", stockValue, "batchCount", stocks.size(), "alerts", alerts);
    }

    public Map<String, Object> hr(Long centerId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        long pendingLeaves = leaveRequestRepository.findByUser_Center_IdAndStatus(centerId, com.hospital.erp.common.enums.LeaveStatus.PENDING).size();
        return Map.of(
                "centerId", centerId,
                "month", month,
                "year", year,
                "periodStart", ym.atDay(1),
                "periodEnd", ym.atEndOfMonth(),
                "pendingLeaves", pendingLeaves
        );
    }

    public List<Map<String, Object>> walletMovement() {
        return walletTransactionRepository.findAll().stream()
                .sorted(Comparator.comparing(transaction -> transaction.getCreatedAt(), Comparator.reverseOrder()))
                .map(transaction -> Map.<String, Object>of(
                        "walletId", transaction.getWallet().getId(),
                        "type", transaction.getType(),
                        "amount", transaction.getAmount(),
                        "referenceId", transaction.getReferenceId(),
                        "createdAt", transaction.getCreatedAt()
                ))
                .toList();
    }

    private String groupKey(Invoice invoice, String groupBy) {
        LocalDate date = invoice.getCreatedAt().toLocalDate();
        if ("MONTH".equalsIgnoreCase(groupBy)) {
            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        }
        if ("WEEK".equalsIgnoreCase(groupBy)) {
            return date.getYear() + "-W" + date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
        return date.toString();
    }
}
