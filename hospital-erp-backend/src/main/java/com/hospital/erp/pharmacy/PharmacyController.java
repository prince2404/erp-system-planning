package com.hospital.erp.pharmacy;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.pharmacy.dto.DispenseRequest;
import com.hospital.erp.pharmacy.dto.DrugRequest;
import com.hospital.erp.pharmacy.dto.DrugStockRequest;
import com.hospital.erp.pharmacy.dto.StockQuantityRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @PostMapping("/drugs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<Drug> createDrug(@Valid @RequestBody DrugRequest request) {
        return ApiResponse.ok(pharmacyService.createDrug(request), "Drug created");
    }

    @GetMapping("/drugs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','PHARMACIST','RECEPTIONIST','CENTER_STAFF')")
    public ApiResponse<List<Drug>> drugs(@RequestParam(required = false) String search, @RequestParam(required = false) String category) {
        return ApiResponse.ok(pharmacyService.drugs(search, category), "Drugs loaded");
    }

    @PostMapping("/drugs/stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<DrugStock> addStock(@Valid @RequestBody DrugStockRequest request) {
        return ApiResponse.ok(pharmacyService.addStock(request), "Stock batch added");
    }

    @GetMapping("/drugs/stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST','CENTER_STAFF')")
    public ApiResponse<List<DrugStock>> stock(@RequestParam Long centerId) {
        return ApiResponse.ok(pharmacyService.stock(centerId), "Stock loaded");
    }

    @PutMapping("/drugs/stock/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<DrugStock> updateStock(@PathVariable Long id, @Valid @RequestBody StockQuantityRequest request) {
        return ApiResponse.ok(pharmacyService.updateQuantity(id, request.quantity()), "Stock quantity updated");
    }

    @PostMapping("/drugs/dispense")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<List<DrugDispense>> dispense(@Valid @RequestBody DispenseRequest request) {
        return ApiResponse.ok(pharmacyService.dispense(request), "Drug dispensed");
    }

    @GetMapping("/drugs/dispense/patient/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','PHARMACIST')")
    public ApiResponse<List<DrugDispense>> dispenseHistory(@PathVariable Long id) {
        return ApiResponse.ok(pharmacyService.patientDispenseHistory(id), "Dispense history loaded");
    }

    @GetMapping("/drugs/alerts")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST','CENTER_STAFF')")
    public ApiResponse<List<StockAlert>> alerts(@RequestParam Long centerId) {
        return ApiResponse.ok(pharmacyService.alerts(centerId), "Stock alerts loaded");
    }

    @PutMapping("/drugs/alerts/{id}/resolve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','PHARMACIST')")
    public ApiResponse<StockAlert> resolveAlert(@PathVariable Long id) {
        return ApiResponse.ok(pharmacyService.resolveAlert(id), "Stock alert resolved");
    }
}
