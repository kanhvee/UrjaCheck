package com.energyassessment.dto;

public class ApplianceAnalysisResponse {

    private String applianceName;
    private long householdCount;
    private long totalQuantity;
    private double averageMonthlyKwh;
    private double totalMonthlyKwh;
    private double averageHoursPerDay;

    public ApplianceAnalysisResponse() {
    }

    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }

    public long getHouseholdCount() { return householdCount; }
    public void setHouseholdCount(long householdCount) { this.householdCount = householdCount; }

    public long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(long totalQuantity) { this.totalQuantity = totalQuantity; }

    public double getAverageMonthlyKwh() { return averageMonthlyKwh; }
    public void setAverageMonthlyKwh(double averageMonthlyKwh) { this.averageMonthlyKwh = averageMonthlyKwh; }

    public double getTotalMonthlyKwh() { return totalMonthlyKwh; }
    public void setTotalMonthlyKwh(double totalMonthlyKwh) { this.totalMonthlyKwh = totalMonthlyKwh; }

    public double getAverageHoursPerDay() { return averageHoursPerDay; }
    public void setAverageHoursPerDay(double averageHoursPerDay) { this.averageHoursPerDay = averageHoursPerDay; }
}
