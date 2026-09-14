package com.energyassessment.dto;

public class EnergySummary {

    private Double dailyKwh;
    private Double monthlyKwh;
    private Double estimatedMonthlyCost;
    private Double referenceMonthlyKwh;
    private Double differenceKwh;
    private Double differencePercentage;
    private String status; // BELOW_REFERENCE | WITHIN_REFERENCE_RANGE | ABOVE_REFERENCE
    private Double tariff;
    private Double actualBill;

    public EnergySummary() {
    }

    public Double getDailyKwh() { return dailyKwh; }
    public void setDailyKwh(Double dailyKwh) { this.dailyKwh = dailyKwh; }

    public Double getMonthlyKwh() { return monthlyKwh; }
    public void setMonthlyKwh(Double monthlyKwh) { this.monthlyKwh = monthlyKwh; }

    public Double getEstimatedMonthlyCost() { return estimatedMonthlyCost; }
    public void setEstimatedMonthlyCost(Double estimatedMonthlyCost) { this.estimatedMonthlyCost = estimatedMonthlyCost; }

    public Double getReferenceMonthlyKwh() { return referenceMonthlyKwh; }
    public void setReferenceMonthlyKwh(Double referenceMonthlyKwh) { this.referenceMonthlyKwh = referenceMonthlyKwh; }

    public Double getDifferenceKwh() { return differenceKwh; }
    public void setDifferenceKwh(Double differenceKwh) { this.differenceKwh = differenceKwh; }

    public Double getDifferencePercentage() { return differencePercentage; }
    public void setDifferencePercentage(Double differencePercentage) { this.differencePercentage = differencePercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTariff() { return tariff; }
    public void setTariff(Double tariff) { this.tariff = tariff; }

    public Double getActualBill() { return actualBill; }
    public void setActualBill(Double actualBill) { this.actualBill = actualBill; }
}
