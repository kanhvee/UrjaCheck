package com.energyassessment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.energyassessment.dto.AdminSummaryResponse;
import com.energyassessment.dto.ApplianceAnalysisResponse;
import com.energyassessment.dto.HouseholdSummaryResponse;
import com.energyassessment.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Community-level analytics, built from real stored assessments")
public class AdminController {

    private final AdminService adminService;

    @Value("${app.admin.key}")
    private String adminKey;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private void verifyAdminKey(String providedKey) {
        if (providedKey == null || !adminKey.equals(providedKey)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid admin key"
            );
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Community-wide summary statistics")
    public AdminSummaryResponse getSummary(
            @RequestHeader(value = "X-Admin-Key", required = false) String providedKey) {

        verifyAdminKey(providedKey);
        return adminService.getSummary();
    }

    @GetMapping("/households")
    @Operation(summary = "Anonymized list of all household assessments")
    public List<HouseholdSummaryResponse> getHouseholds(
            @RequestHeader(value = "X-Admin-Key", required = false) String providedKey) {

        verifyAdminKey(providedKey);
        return adminService.getHouseholds();
    }

    @GetMapping("/appliances")
    @Operation(summary = "Aggregated appliance-level community analysis")
    public List<ApplianceAnalysisResponse> getApplianceAnalysis(
            @RequestHeader(value = "X-Admin-Key", required = false) String providedKey) {

        verifyAdminKey(providedKey);
        return adminService.getApplianceAnalysis();
    }
}