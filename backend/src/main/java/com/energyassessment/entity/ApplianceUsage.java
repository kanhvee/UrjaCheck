package com.energyassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "appliance_usage")
public class ApplianceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "appliance_name", nullable = false, length = 80)
    private String applianceName;

    @Column(name = "appliance_category", length = 60)
    private String applianceCategory;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "power_watts", nullable = false)
    private Double powerWatts;

    @Column(name = "hours_per_day", nullable = false)
    private Double hoursPerDay;

    @Column(name = "daily_kwh", nullable = false)
    private Double dailyKwh;

    @Column(name = "monthly_kwh", nullable = false)
    private Double monthlyKwh;

    @Column(name = "reference_hours_per_day")
    private Double referenceHoursPerDay;

    @Column(name = "reference_monthly_kwh")
    private Double referenceMonthlyKwh;

    @Column(name = "difference_kwh")
    private Double differenceKwh;

    @Column(name = "difference_percentage")
    private Double differencePercentage;

    // BELOW_REFERENCE / WITHIN_REFERENCE_RANGE / ABOVE_REFERENCE
    @Column(name = "status", length = 40)
    private String status;

    public ApplianceUsage() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }

    public String getApplianceCategory() { return applianceCategory; }
    public void setApplianceCategory(String applianceCategory) { this.applianceCategory = applianceCategory; }

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
}
