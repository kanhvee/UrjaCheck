package com.energyassessment.service;

import com.energyassessment.dto.AdminSummaryResponse;
import com.energyassessment.dto.ApplianceAnalysisResponse;
import com.energyassessment.dto.HouseholdSummaryResponse;
import com.energyassessment.entity.ApplianceUsage;
import com.energyassessment.entity.Assessment;
import com.energyassessment.repository.ApplianceUsageRepository;
import com.energyassessment.repository.AssessmentRepository;
import com.energyassessment.util.EnergyCalculationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final AssessmentRepository assessmentRepository;
    private final ApplianceUsageRepository applianceUsageRepository;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public AdminService(AssessmentRepository assessmentRepository, ApplianceUsageRepository applianceUsageRepository) {
        this.assessmentRepository = assessmentRepository;
        this.applianceUsageRepository = applianceUsageRepository;
    }

    public AdminSummaryResponse getSummary() {
        List<Assessment> all = assessmentRepository.findAll();
        AdminSummaryResponse summary = new AdminSummaryResponse();

        if (all.isEmpty()) {
            return summary; // all zeros — the frontend should render an empty state
        }

        long total = all.size();
        double totalMonthly = all.stream().mapToDouble(Assessment::getTotalMonthlyKwh).sum();
        double totalReference = all.stream().mapToDouble(Assessment::getReferenceMonthlyKwh).sum();
        double totalCost = all.stream().mapToDouble(Assessment::getEstimatedMonthlyCost).sum();

        summary.setTotalAssessments(total);
        summary.setAverageMonthlyConsumption(round(totalMonthly / total));
        summary.setAverageReferenceConsumption(round(totalReference / total));
        summary.setTotalCommunityConsumption(round(totalMonthly));
        summary.setAverageEstimatedCost(round(totalCost / total));
        summary.setHouseholdsAboveReference(assessmentRepository.countByStatus(EnergyCalculationUtil.STATUS_ABOVE));
        summary.setHouseholdsWithinReference(assessmentRepository.countByStatus(EnergyCalculationUtil.STATUS_WITHIN));
        summary.setHouseholdsBelowReference(assessmentRepository.countByStatus(EnergyCalculationUtil.STATUS_BELOW));
        return summary;
    }

    public List<HouseholdSummaryResponse> getHouseholds() {
        return assessmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Assessment::getCreatedAt).reversed())
                .map(this::toHouseholdSummary)
                .toList();
    }

    private HouseholdSummaryResponse toHouseholdSummary(Assessment a) {
        HouseholdSummaryResponse h = new HouseholdSummaryResponse();
        h.setAssessmentId(a.getAssessmentUuid());
        h.setHouseholdSize(a.getHouseholdSize());
        h.setHouseType(a.getHouseType());
        h.setTotalMonthlyKwh(a.getTotalMonthlyKwh());
        h.setReferenceMonthlyKwh(a.getReferenceMonthlyKwh());
        h.setDifferenceKwh(a.getDifferenceKwh());
        h.setDifferencePercentage(a.getDifferencePercentage());
        h.setStatus(a.getStatus());
        h.setEstimatedMonthlyCost(a.getEstimatedMonthlyCost());
        h.setCreatedAt(a.getCreatedAt() != null ? a.getCreatedAt().format(TIMESTAMP_FORMAT) : null);
        return h;
    }

    public List<ApplianceAnalysisResponse> getApplianceAnalysis() {
        List<ApplianceUsage> all = applianceUsageRepository.findAll();

        Map<String, List<ApplianceUsage>> byName = new LinkedHashMap<>();
        for (ApplianceUsage u : all) {
            byName.computeIfAbsent(u.getApplianceName(), k -> new java.util.ArrayList<>()).add(u);
        }

        return byName.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<ApplianceUsage> usages = entry.getValue();
                    long householdCount = usages.stream()
                            .map(u -> u.getAssessment().getId())
                            .distinct()
                            .count();
                    long totalQuantity = usages.stream().mapToLong(ApplianceUsage::getQuantity).sum();
                    double totalMonthly = usages.stream().mapToDouble(ApplianceUsage::getMonthlyKwh).sum();
                    double avgMonthly = totalMonthly / usages.size();
                    double avgHours = usages.stream().mapToDouble(ApplianceUsage::getHoursPerDay).average().orElse(0);

                    ApplianceAnalysisResponse r = new ApplianceAnalysisResponse();
                    r.setApplianceName(name);
                    r.setHouseholdCount(householdCount);
                    r.setTotalQuantity(totalQuantity);
                    r.setAverageMonthlyKwh(round(avgMonthly));
                    r.setTotalMonthlyKwh(round(totalMonthly));
                    r.setAverageHoursPerDay(round(avgHours));
                    return r;
                })
                .sorted(Comparator.comparingDouble(ApplianceAnalysisResponse::getTotalMonthlyKwh).reversed())
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
