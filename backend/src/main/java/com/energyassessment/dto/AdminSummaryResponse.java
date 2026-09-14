package com.energyassessment.dto;

public class AdminSummaryResponse {

    private long totalAssessments;
    private double averageMonthlyConsumption;
    private double averageReferenceConsumption;
    private double totalCommunityConsumption;
    private double averageEstimatedCost;
    private long householdsAboveReference;
    private long householdsWithinReference;
    private long householdsBelowReference;

    public AdminSummaryResponse() {
    }

    public long getTotalAssessments() { return totalAssessments; }
    public void setTotalAssessments(long totalAssessments) { this.totalAssessments = totalAssessments; }

    public double getAverageMonthlyConsumption() { return averageMonthlyConsumption; }
    public void setAverageMonthlyConsumption(double averageMonthlyConsumption) { this.averageMonthlyConsumption = averageMonthlyConsumption; }

    public double getAverageReferenceConsumption() { return averageReferenceConsumption; }
    public void setAverageReferenceConsumption(double averageReferenceConsumption) { this.averageReferenceConsumption = averageReferenceConsumption; }

    public double getTotalCommunityConsumption() { return totalCommunityConsumption; }
    public void setTotalCommunityConsumption(double totalCommunityConsumption) { this.totalCommunityConsumption = totalCommunityConsumption; }

    public double getAverageEstimatedCost() { return averageEstimatedCost; }
    public void setAverageEstimatedCost(double averageEstimatedCost) { this.averageEstimatedCost = averageEstimatedCost; }

    public long getHouseholdsAboveReference() { return householdsAboveReference; }
    public void setHouseholdsAboveReference(long householdsAboveReference) { this.householdsAboveReference = householdsAboveReference; }

    public long getHouseholdsWithinReference() { return householdsWithinReference; }
    public void setHouseholdsWithinReference(long householdsWithinReference) { this.householdsWithinReference = householdsWithinReference; }

    public long getHouseholdsBelowReference() { return householdsBelowReference; }
    public void setHouseholdsBelowReference(long householdsBelowReference) { this.householdsBelowReference = householdsBelowReference; }
}
