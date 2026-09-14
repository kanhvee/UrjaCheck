package com.energyassessment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A single household energy assessment.
 * Uses an anonymous UUID (assessmentUuid) as the public-facing identifier
 * so the internal auto-increment primary key is never exposed to the frontend.
 */
@Entity
@Table(name = "assessment")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessment_uuid", unique = true, nullable = false, length = 40)
    private String assessmentUuid;

    @Column(name = "household_size", nullable = false)
    private Integer householdSize;

    @Column(name = "house_type", length = 20)
    private String houseType;

    // Optional, resident-reported bill. Used only for comparison, never required.
    @Column(name = "actual_bill")
    private Double actualBill;

    @Column(name = "tariff", nullable = false)
    private Double tariff;

    @Column(name = "total_daily_kwh", nullable = false)
    private Double totalDailyKwh;

    @Column(name = "total_monthly_kwh", nullable = false)
    private Double totalMonthlyKwh;

    @Column(name = "estimated_monthly_cost", nullable = false)
    private Double estimatedMonthlyCost;

    @Column(name = "reference_monthly_kwh", nullable = false)
    private Double referenceMonthlyKwh;

    @Column(name = "difference_kwh", nullable = false)
    private Double differenceKwh;

    @Column(name = "difference_percentage", nullable = false)
    private Double differencePercentage;

    // WITHIN_REFERENCE_RANGE / BELOW_REFERENCE / ABOVE_REFERENCE
    @Column(name = "assessment_status", length = 40, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApplianceUsage> appliances = new ArrayList<>();

    public Assessment() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // --- getters / setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssessmentUuid() { return assessmentUuid; }
    public void setAssessmentUuid(String assessmentUuid) { this.assessmentUuid = assessmentUuid; }

    public Integer getHouseholdSize() { return householdSize; }
    public void setHouseholdSize(Integer householdSize) { this.householdSize = householdSize; }

    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }

    public Double getActualBill() { return actualBill; }
    public void setActualBill(Double actualBill) { this.actualBill = actualBill; }

    public Double getTariff() { return tariff; }
    public void setTariff(Double tariff) { this.tariff = tariff; }

    public Double getTotalDailyKwh() { return totalDailyKwh; }
    public void setTotalDailyKwh(Double totalDailyKwh) { this.totalDailyKwh = totalDailyKwh; }

    public Double getTotalMonthlyKwh() { return totalMonthlyKwh; }
    public void setTotalMonthlyKwh(Double totalMonthlyKwh) { this.totalMonthlyKwh = totalMonthlyKwh; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ApplianceUsage> getAppliances() { return appliances; }
    public void setAppliances(List<ApplianceUsage> appliances) { this.appliances = appliances; }

    public void addAppliance(ApplianceUsage usage) {
        appliances.add(usage);
        usage.setAssessment(this);
    }
}
