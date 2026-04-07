package com.hospital.erp.pharmacy;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.NotificationType;
import com.hospital.erp.common.enums.StockAlertType;
import com.hospital.erp.geographic.entities.Center;
import com.hospital.erp.geographic.repositories.CenterRepository;
import com.hospital.erp.notification.NotificationService;
import com.hospital.erp.patient.OpdVisit;
import com.hospital.erp.patient.OpdVisitRepository;
import com.hospital.erp.patient.Patient;
import com.hospital.erp.patient.PatientRepository;
import com.hospital.erp.pharmacy.dto.DispenseRequest;
import com.hospital.erp.pharmacy.dto.DrugRequest;
import com.hospital.erp.pharmacy.dto.DrugStockRequest;
import com.hospital.erp.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private static final int LOW_STOCK_THRESHOLD = 10;

    private final DrugRepository drugRepository;
    private final DrugStockRepository drugStockRepository;
    private final DrugDispenseRepository drugDispenseRepository;
    private final StockAlertRepository stockAlertRepository;
    private final PatientRepository patientRepository;
    private final OpdVisitRepository opdVisitRepository;
    private final CenterRepository centerRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @Transactional
    public Drug createDrug(DrugRequest request) {
        Drug drug = new Drug();
        drug.setName(request.name());
        drug.setGenericName(request.genericName());
        drug.setCategory(request.category());
        drug.setUnit(request.unit());
        drug.setHsnCode(request.hsnCode());
        drug.setManufacturer(request.manufacturer());
        drug.setActive(true);
        return drugRepository.save(drug);
    }

    public List<Drug> drugs(String search, String category) {
        if (search != null && !search.isBlank()) {
            return drugRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);
        }
        if (category != null && !category.isBlank()) {
            return drugRepository.findByCategoryAndActiveTrue(category);
        }
        return drugRepository.findByActiveTrue();
    }

    @Transactional
    public DrugStock addStock(DrugStockRequest request) {
        DrugStock stock = new DrugStock();
        stock.setDrug(findDrug(request.drugId()));
        stock.setCenter(findCenter(request.centerId()));
        stock.setBatchNumber(request.batchNumber());
        stock.setExpiryDate(request.expiryDate());
        stock.setQuantity(request.quantity());
        stock.setPurchasePrice(request.purchasePrice() != null ? request.purchasePrice() : BigDecimal.ZERO);
        stock.setSellingPrice(request.sellingPrice() != null ? request.sellingPrice() : BigDecimal.ZERO);
        stock.setSupplier(request.supplier());
        stock.setReceivedDate(request.receivedDate() != null ? request.receivedDate() : LocalDate.now());
        DrugStock saved = drugStockRepository.save(stock);
        evaluateStock(saved);
        return saved;
    }

    public List<DrugStock> stock(Long centerId) {
        return drugStockRepository.findByCenter_Id(centerId);
    }

    @Transactional
    public DrugStock updateQuantity(Long id, Integer quantity) {
        DrugStock stock = drugStockRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Drug stock not found"));
        stock.setQuantity(quantity);
        DrugStock saved = drugStockRepository.save(stock);
        evaluateStock(saved);
        return saved;
    }

    @Transactional
    public List<DrugDispense> dispense(DispenseRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));
        Drug drug = findDrug(request.drugId());
        OpdVisit opdVisit = request.opdVisitId() != null
                ? opdVisitRepository.findById(request.opdVisitId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "OPD visit not found"))
                : null;

        int remaining = request.quantity();
        List<DrugStock> stocks = drugStockRepository.findByDrug_IdAndCenter_IdAndQuantityGreaterThanOrderByExpiryDateAsc(
                request.drugId(), request.centerId(), 0);
        int available = stocks.stream().mapToInt(DrugStock::getQuantity).sum();
        if (available < remaining) {
            throw new AppException(HttpStatus.CONFLICT, "Insufficient stock. Available quantity: " + available);
        }

        List<DrugDispense> dispenses = new ArrayList<>();
        for (DrugStock stock : stocks) {
            if (remaining == 0) {
                break;
            }
            int quantity = Math.min(remaining, stock.getQuantity());
            stock.setQuantity(stock.getQuantity() - quantity);
            drugStockRepository.save(stock);

            DrugDispense dispense = new DrugDispense();
            dispense.setPatient(patient);
            dispense.setOpdVisit(opdVisit);
            dispense.setDrugStock(stock);
            dispense.setDrug(drug);
            dispense.setQuantity(quantity);
            dispense.setUnitPrice(stock.getSellingPrice() != null ? stock.getSellingPrice() : BigDecimal.ZERO);
            dispense.setTotalPrice(dispense.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            dispense.setDispensedBy(currentUserService.get());
            dispense.setDispensedAt(LocalDateTime.now());
            dispenses.add(drugDispenseRepository.save(dispense));
            evaluateStock(stock);
            remaining -= quantity;
        }
        return dispenses;
    }

    public List<DrugDispense> patientDispenseHistory(Long patientId) {
        return drugDispenseRepository.findByPatient_IdOrderByDispensedAtDesc(patientId);
    }

    public List<StockAlert> alerts(Long centerId) {
        return stockAlertRepository.findByCenter_IdAndResolvedFalse(centerId);
    }

    @Transactional
    public StockAlert resolveAlert(Long id) {
        StockAlert alert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Stock alert not found"));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return stockAlertRepository.save(alert);
    }

    private void evaluateStock(DrugStock stock) {
        if (stock.getQuantity() <= 0) {
            createAlert(stock, StockAlertType.OUT_OF_STOCK);
        } else if (stock.getQuantity() < LOW_STOCK_THRESHOLD) {
            createAlert(stock, StockAlertType.LOW_STOCK);
        }
        if (stock.getExpiryDate() != null && stock.getExpiryDate().isBefore(LocalDate.now().plusDays(30))) {
            createAlert(stock, StockAlertType.EXPIRY_SOON);
        }
    }

    private void createAlert(DrugStock stock, StockAlertType type) {
        StockAlert alert = new StockAlert();
        alert.setDrug(stock.getDrug());
        alert.setCenter(stock.getCenter());
        alert.setAlertType(type);
        alert.setCurrentQuantity(stock.getQuantity());
        alert.setExpiryDate(stock.getExpiryDate());
        alert.setTriggeredAt(LocalDateTime.now());
        alert.setResolved(false);
        StockAlert saved = stockAlertRepository.save(alert);
        notificationService.notifyCenter(
                stock.getCenter().getId(),
                NotificationType.LOW_STOCK,
                "Stock alert: " + stock.getDrug().getName(),
                type + " for batch " + stock.getBatchNumber(),
                saved.getId(),
                "STOCK_ALERT"
        );
    }

    private Drug findDrug(Long id) {
        return drugRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Drug not found"));
    }

    private Center findCenter(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Center not found"));
    }
}
