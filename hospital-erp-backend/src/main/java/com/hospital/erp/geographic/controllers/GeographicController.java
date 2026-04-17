package com.hospital.erp.geographic.controllers;

import com.hospital.erp.common.ApiResponse;
import com.hospital.erp.geographic.dto.BlockRequest;
import com.hospital.erp.geographic.dto.BlockResponse;
import com.hospital.erp.geographic.dto.CenterRequest;
import com.hospital.erp.geographic.dto.CenterResponse;
import com.hospital.erp.geographic.dto.DistrictRequest;
import com.hospital.erp.geographic.dto.DistrictResponse;
import com.hospital.erp.geographic.dto.StateRequest;
import com.hospital.erp.geographic.dto.StateResponse;
import com.hospital.erp.geographic.services.GeographicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class GeographicController {
    private final GeographicService geographicService;

    @GetMapping("/states")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<StateResponse>> states() {
        return ApiResponse.ok(geographicService.states().stream().map(StateResponse::from).toList(), "States loaded");
    }

    @PostMapping("/states")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<StateResponse> createState(@Valid @RequestBody StateRequest request) {
        return ApiResponse.ok(StateResponse.from(geographicService.createState(request)), "State created");
    }

    @GetMapping("/states/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<StateResponse> state(@PathVariable Long id) {
        return ApiResponse.ok(StateResponse.from(geographicService.state(id)), "State loaded");
    }

    @PutMapping("/states/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<StateResponse> updateState(@PathVariable Long id, @Valid @RequestBody StateRequest request) {
        return ApiResponse.ok(StateResponse.from(geographicService.updateState(id, request)), "State updated");
    }

    @DeleteMapping("/states/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteState(@PathVariable Long id) {
        geographicService.deleteState(id);
        return ApiResponse.ok(null, "State deleted");
    }

    @GetMapping("/districts")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<DistrictResponse>> districts(@RequestParam(required = false) Long stateId) {
        return ApiResponse.ok(geographicService.districts(stateId).stream().map(DistrictResponse::from).toList(), "Districts loaded");
    }

    @PostMapping("/districts")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DistrictResponse> createDistrict(@Valid @RequestBody DistrictRequest request) {
        return ApiResponse.ok(DistrictResponse.from(geographicService.createDistrict(request)), "District created");
    }

    @GetMapping("/districts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DistrictResponse> district(@PathVariable Long id) {
        return ApiResponse.ok(DistrictResponse.from(geographicService.district(id)), "District loaded");
    }

    @PutMapping("/districts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DistrictResponse> updateDistrict(@PathVariable Long id, @Valid @RequestBody DistrictRequest request) {
        return ApiResponse.ok(DistrictResponse.from(geographicService.updateDistrict(id, request)), "District updated");
    }

    @DeleteMapping("/districts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteDistrict(@PathVariable Long id) {
        geographicService.deleteDistrict(id);
        return ApiResponse.ok(null, "District deleted");
    }

    @GetMapping("/blocks")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BlockResponse>> blocks(@RequestParam(required = false) Long districtId) {
        return ApiResponse.ok(geographicService.blocks(districtId).stream().map(BlockResponse::from).toList(), "Blocks loaded");
    }

    @PostMapping("/blocks")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BlockResponse> createBlock(@Valid @RequestBody BlockRequest request) {
        return ApiResponse.ok(BlockResponse.from(geographicService.createBlock(request)), "Block created");
    }

    @GetMapping("/blocks/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BlockResponse> block(@PathVariable Long id) {
        return ApiResponse.ok(BlockResponse.from(geographicService.block(id)), "Block loaded");
    }

    @PutMapping("/blocks/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BlockResponse> updateBlock(@PathVariable Long id, @Valid @RequestBody BlockRequest request) {
        return ApiResponse.ok(BlockResponse.from(geographicService.updateBlock(id, request)), "Block updated");
    }

    @DeleteMapping("/blocks/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteBlock(@PathVariable Long id) {
        geographicService.deleteBlock(id);
        return ApiResponse.ok(null, "Block deleted");
    }

    @GetMapping("/centers")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<CenterResponse>> centers() {
        return ApiResponse.ok(geographicService.centers().stream().map(CenterResponse::from).toList(), "Centers loaded");
    }

    @GetMapping("/centers/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CenterResponse> center(@PathVariable Long id) {
        return ApiResponse.ok(CenterResponse.from(geographicService.center(id)), "Center loaded");
    }

    @PostMapping("/centers")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CenterResponse> createCenter(@Valid @RequestBody CenterRequest request) {
        return ApiResponse.ok(CenterResponse.from(geographicService.createCenter(request)), "Center created");
    }

    @PutMapping("/centers/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CenterResponse> updateCenter(@PathVariable Long id, @Valid @RequestBody CenterRequest request) {
        return ApiResponse.ok(CenterResponse.from(geographicService.updateCenter(id, request)), "Center updated");
    }

    @DeleteMapping("/centers/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CenterResponse> deactivateCenter(@PathVariable Long id) {
        return ApiResponse.ok(CenterResponse.from(geographicService.deactivateCenter(id)), "Center deactivated");
    }
}
