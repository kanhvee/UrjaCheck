package com.energyassessment.dto;

public class ApplianceResult {

    private String applianceName;
    private String category;
    private Integer quantity;
    private Double powerWatts;
    private Double hoursPerDay;
    private Double dailyKwh;
    private Double monthlyKwh;
    private Double referenceHoursPerDay;
    private Double referenceMonthlyKwh;
    private Double differenceKwh;
    private Double differencePercentage;
    private String status; // BELOW_REFERENCE | WITHIN_REFERENCE_RANGE | ABOVE_REFERENCE
    private String recommendation;
    private ReferenceSourceDto referenceSource;

    public ApplianceResult() {
    }

    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPowerWatts() { return powerWatts; }
    public void setPowerWatts(Double powerWatts) { this.powerWatts = powerWatts; }

    public Double getHoursPerDay() { return hoursPerDay; }
    public void setHoursPerDay(Double hoursPerDay) { this.hoursPerDay = hoursPerDay; }

    public Double getDailyKwh() { return dailyKwh; }
    public void setDailyKwh(Double dailyKwh) { this.dailyKwh = dailyKwh; }

    public Double getMonthlyKwh() { return monthlyKwh; }
    public void setMonthlyKwh(Double monthlyKwh) { this.monthlyKwh = monthlyKwh; }

    public Double getReferenceHoursPerDay() { return referenceHoursPerDay; }
    public void setReferenceHoursPerDay(Double referenceHoursPerDay) { this.referenceHoursPerDay = referenceHoursPerDay; }

    public Double getReferenceMonthlyKwh() { return referenceMonthlyKwh; }
    public void setReferenceMonthlyKwh(Double referenceMonthlyKwh) { this.referenceMonthlyKwh = referenceMonthlyKwh; }

    public Double getDifferenceKwh() { return differenceKwh; }
    public void setDifferenceKwh(Double differenceKwh) { this.differenceKwh = differenceKwh; }

    public Double getDifferencePercentage() { return differencePercentage; }
    public void setDifferencePercentage(Double differencePercentage) { this.differencePercentage = differencePercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public ReferenceSourceDto getReferenceSource() { return referenceSource; }
    public void setReferenceSource(ReferenceSourceDto referenceSource) { this.referenceSource = referenceSource; }
}
