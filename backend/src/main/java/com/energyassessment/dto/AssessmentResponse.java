package com.energyassessment.dto;

import java.util.List;

public class AssessmentResponse {

    private String assessmentId;
    private Integer householdSize;
    private String houseType;
    private String createdAt;
    private EnergySummary summary;
    private List<ApplianceResult> appliances;

    public AssessmentResponse() {
    }

    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }

    public Integer getHouseholdSize() { return householdSize; }
    public void setHouseholdSize(Integer householdSize) { this.householdSize = householdSize; }

    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public EnergySummary getSummary() { return summary; }
    public void setSummary(EnergySummary summary) { this.summary = summary; }

    public List<ApplianceResult> getAppliances() { return appliances; }
    public void setAppliances(List<ApplianceResult> appliances) { this.appliances = appliances; }
}
