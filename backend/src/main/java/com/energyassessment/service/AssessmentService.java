package com.energyassessment.service;

import com.energyassessment.dto.*;
import com.energyassessment.entity.ApplianceUsage;
import com.energyassessment.entity.Assessment;
import com.energyassessment.exception.ResourceNotFoundException;
import com.energyassessment.repository.AssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final EnergyCalculationService energyCalculationService;
    private final ReferenceStandardService referenceStandardService;
    private final RecommendationService recommendationService;

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public AssessmentService(AssessmentRepository assessmentRepository,
                              EnergyCalculationService energyCalculationService,
                              ReferenceStandardService referenceStandardService,
                              RecommendationService recommendationService) {
        this.assessmentRepository = assessmentRepository;
        this.energyCalculationService = energyCalculationService;
        this.referenceStandardService = referenceStandardService;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public AssessmentResponse createAssessment(AssessmentRequest request) {
        double tariff = energyCalculationService.resolveTariff(request.getTariff());

        List<ApplianceResult> results = new ArrayList<>();
        for (ApplianceRequest applianceReq : request.getAppliances()) {
            results.add(energyCalculationService.calculateAppliance(applianceReq));
        }
        EnergySummary summary = energyCalculationService.summarize(results, tariff, request.getActualBill());

        Assessment assessment = new Assessment();
        assessment.setAssessmentUuid(generateAssessmentId());
        assessment.setHouseholdSize(request.getHouseholdSize());
        assessment.setHouseType(request.getHouseType());
        assessment.setActualBill(request.getActualBill());
        assessment.setTariff(tariff);
        assessment.setTotalDailyKwh(summary.getDailyKwh());
        assessment.setTotalMonthlyKwh(summary.getMonthlyKwh());
        assessment.setEstimatedMonthlyCost(summary.getEstimatedMonthlyCost());
        assessment.setReferenceMonthlyKwh(summary.getReferenceMonthlyKwh());
        assessment.setDifferenceKwh(summary.getDifferenceKwh());
        assessment.setDifferencePercentage(summary.getDifferencePercentage());
        assessment.setStatus(summary.getStatus());

        for (ApplianceResult r : results) {
            ApplianceUsage usage = new ApplianceUsage();
            usage.setApplianceName(r.getApplianceName());
            usage.setApplianceCategory(r.getCategory());
            usage.setQuantity(r.getQuantity());
            usage.setPowerWatts(r.getPowerWatts());
            usage.setHoursPerDay(r.getHoursPerDay());
            usage.setDailyKwh(r.getDailyKwh());
            usage.setMonthlyKwh(r.getMonthlyKwh());
            usage.setReferenceHoursPerDay(r.getReferenceHoursPerDay());
            usage.setReferenceMonthlyKwh(r.getReferenceMonthlyKwh());
            usage.setDifferenceKwh(r.getDifferenceKwh());
            usage.setDifferencePercentage(r.getDifferencePercentage());
            usage.setStatus(r.getStatus());
            assessment.addAppliance(usage);
        }

        Assessment saved = assessmentRepository.save(assessment);
        return toResponse(saved, results);
    }

    @Transactional(readOnly = true)
    public AssessmentResponse getAssessment(String assessmentUuid) {
        Assessment assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentUuid));

        List<ApplianceResult> results = new ArrayList<>();
        for (ApplianceUsage usage : assessment.getAppliances()) {
            ApplianceResult r = new ApplianceResult();
            r.setApplianceName(usage.getApplianceName());
            r.setCategory(usage.getApplianceCategory());
            r.setQuantity(usage.getQuantity());
            r.setPowerWatts(usage.getPowerWatts());
            r.setHoursPerDay(usage.getHoursPerDay());
            r.setDailyKwh(usage.getDailyKwh());
            r.setMonthlyKwh(usage.getMonthlyKwh());
            r.setReferenceHoursPerDay(usage.getReferenceHoursPerDay());
            r.setReferenceMonthlyKwh(usage.getReferenceMonthlyKwh());
            r.setDifferenceKwh(usage.getDifferenceKwh());
            r.setDifferencePercentage(usage.getDifferencePercentage());
            r.setStatus(usage.getStatus());
            r.setReferenceSource(referenceStandardService.getReferenceSource(usage.getApplianceName()));
            r.setRecommendation(recommendationService.getRecommendation(usage.getApplianceName(), usage.getStatus()));
            results.add(r);
        }
        return toResponse(assessment, results);
    }

    private AssessmentResponse toResponse(Assessment assessment, List<ApplianceResult> results) {
        AssessmentResponse response = new AssessmentResponse();
        response.setAssessmentId(assessment.getAssessmentUuid());
        response.setHouseholdSize(assessment.getHouseholdSize());
        response.setHouseType(assessment.getHouseType());
        response.setCreatedAt(assessment.getCreatedAt() != null ? assessment.getCreatedAt().format(TIMESTAMP_FORMAT) : null);

        EnergySummary summary = new EnergySummary();
        summary.setDailyKwh(assessment.getTotalDailyKwh());
        summary.setMonthlyKwh(assessment.getTotalMonthlyKwh());
        summary.setEstimatedMonthlyCost(assessment.getEstimatedMonthlyCost());
        summary.setReferenceMonthlyKwh(assessment.getReferenceMonthlyKwh());
        summary.setDifferenceKwh(assessment.getDifferenceKwh());
        summary.setDifferencePercentage(assessment.getDifferencePercentage());
        summary.setStatus(assessment.getStatus());
        summary.setTariff(assessment.getTariff());
        summary.setActualBill(assessment.getActualBill());
        response.setSummary(summary);

        response.setAppliances(results);
        return response;
    }

    /** Anonymous, human-readable-ish identifier — no sequential DB IDs exposed. */
    private String generateAssessmentId() {
        return "UC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
