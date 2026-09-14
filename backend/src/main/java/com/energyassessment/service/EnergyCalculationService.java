package com.energyassessment.service;

import com.energyassessment.dto.ApplianceRequest;
import com.energyassessment.dto.ApplianceResult;
import com.energyassessment.dto.EnergySummary;
import com.energyassessment.util.EnergyCalculationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyCalculationService {

    private final ReferenceStandardService referenceStandardService;
    private final RecommendationService recommendationService;

    // Illustrative tariff assumption, overridable via application.properties
    // or per-request. NOT a real government/DISCOM tariff.
    @Value("${urjacheck.default-tariff:8.0}")
    private double defaultTariff;

    public EnergyCalculationService(ReferenceStandardService referenceStandardService,
                                     RecommendationService recommendationService) {
        this.referenceStandardService = referenceStandardService;
        this.recommendationService = recommendationService;
    }

    public double resolveTariff(Double requestedTariff) {
        return (requestedTariff != null && requestedTariff > 0) ? requestedTariff : defaultTariff;
    }

    public ApplianceResult calculateAppliance(ApplianceRequest req) {
        double daily = EnergyCalculationUtil.dailyEnergyKWh(req.getQuantity(), req.getPowerWatts(), req.getHoursPerDay());
        double monthly = EnergyCalculationUtil.monthlyEnergyKWh(daily);

        double referenceHours = referenceStandardService.getReferenceHours(req.getApplianceName());
        double referenceMonthly = EnergyCalculationUtil.referenceEnergyKWh(req.getQuantity(), req.getPowerWatts(), referenceHours);

        double diffKwh = EnergyCalculationUtil.differenceKWh(monthly, referenceMonthly);
        double diffPct = EnergyCalculationUtil.percentageDifference(monthly, referenceMonthly);
        String status = EnergyCalculationUtil.classify(monthly, referenceMonthly);

        ApplianceResult result = new ApplianceResult();
        result.setApplianceName(req.getApplianceName());
        result.setCategory(req.getCategory());
        result.setQuantity(req.getQuantity());
        result.setPowerWatts(req.getPowerWatts());
        result.setHoursPerDay(req.getHoursPerDay());
        result.setDailyKwh(daily);
        result.setMonthlyKwh(monthly);
        result.setReferenceHoursPerDay(referenceHours);
        result.setReferenceMonthlyKwh(referenceMonthly);
        result.setDifferenceKwh(diffKwh);
        result.setDifferencePercentage(diffPct);
        result.setStatus(status);
        result.setRecommendation(recommendationService.getRecommendation(req.getApplianceName(), status));
        result.setReferenceSource(referenceStandardService.getReferenceSource(req.getApplianceName()));
        return result;
    }

    public EnergySummary summarize(List<ApplianceResult> results, double tariff, Double actualBill) {
        double totalDaily = round(results.stream().mapToDouble(ApplianceResult::getDailyKwh).sum());
        double totalMonthly = round(results.stream().mapToDouble(ApplianceResult::getMonthlyKwh).sum());
        double totalReference = round(results.stream().mapToDouble(ApplianceResult::getReferenceMonthlyKwh).sum());
        double cost = EnergyCalculationUtil.monthlyCost(totalMonthly, tariff);
        double diffKwh = EnergyCalculationUtil.differenceKWh(totalMonthly, totalReference);
        double diffPct = EnergyCalculationUtil.percentageDifference(totalMonthly, totalReference);
        String status = EnergyCalculationUtil.classify(totalMonthly, totalReference);

        EnergySummary summary = new EnergySummary();
        summary.setDailyKwh(totalDaily);
        summary.setMonthlyKwh(totalMonthly);
        summary.setEstimatedMonthlyCost(cost);
        summary.setReferenceMonthlyKwh(totalReference);
        summary.setDifferenceKwh(diffKwh);
        summary.setDifferencePercentage(diffPct);
        summary.setStatus(status);
        summary.setTariff(tariff);
        summary.setActualBill(actualBill);
        return summary;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
