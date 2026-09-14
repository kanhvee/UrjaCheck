package com.energyassessment.dto;

public class HouseholdSummaryResponse {

    private String assessmentId;
    private Integer householdSize;
    private String houseType;
    private Double totalMonthlyKwh;
    private Double referenceMonthlyKwh;
    private Double differenceKwh;
    private Double differencePercentage;
    private String status;
    private Double estimatedMonthlyCost;
    private String createdAt;

    public HouseholdSummaryResponse() {
    }

    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }

    public Integer getHouseholdSize() { return householdSize; }
    public void setHouseholdSize(Integer householdSize) { this.householdSize = householdSize; }

    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }

    public Double getTotalMonthlyKwh() { return totalMonthlyKwh; }
    public void setTotalMonthlyKwh(Double totalMonthlyKwh) { this.totalMonthlyKwh = totalMonthlyKwh; }

    public Double getReferenceMonthlyKwh() { return referenceMonthlyKwh; }
    public void setReferenceMonthlyKwh(Double referenceMonthlyKwh) { this.referenceMonthlyKwh = referenceMonthlyKwh; }

    public Double getDifferenceKwh() { return differenceKwh; }
    public void setDifferenceKwh(Double differenceKwh) { this.differenceKwh = differenceKwh; }

    public Double getDifferencePercentage() { return differencePercentage; }
    public void setDifferencePercentage(Double differencePercentage) { this.differencePercentage = differencePercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getEstimatedMonthlyCost() { return estimatedMonthlyCost; }
    public void setEstimatedMonthlyCost(Double estimatedMonthlyCost) { this.estimatedMonthlyCost = estimatedMonthlyCost; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
