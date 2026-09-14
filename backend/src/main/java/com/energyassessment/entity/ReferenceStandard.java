package com.energyassessment.entity;

import jakarta.persistence.*;

/**
 * A benchmark/reference row for one appliance.
 *
 * IMPORTANT — reference_type is the transparency mechanism required by the
 * project brief. It must always be one of:
 *   GOVERNMENT_MANDATED   - an official, regulated limit (rare for household appliances)
 *   BEE_BENCHMARK         - a published Bureau of Energy Efficiency benchmark figure
 *   STAR_LABEL_INFO       - general BEE star-labelling / efficiency information (no numeric target)
 *   PROJECT_REFERENCE     - a value defined by this project for awareness/comparison only
 *
 * Do not seed BEE_BENCHMARK or GOVERNMENT_MANDATED rows with invented numbers.
 * Unless a real, citable BEE/ENS figure is entered by an administrator, every
 * seeded row in this table ships as PROJECT_REFERENCE — see data.sql.
 */
@Entity
@Table(name = "reference_standard")
public class ReferenceStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appliance_category", length = 60)
    private String applianceCategory;

    @Column(name = "appliance_name", nullable = false, length = 80)
    private String applianceName;

    @Column(name = "reference_type", nullable = false, length = 30)
    private String referenceType;

    @Column(name = "reference_value")
    private Double referenceValue;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "reference_hours_per_day")
    private Double referenceHoursPerDay;

    @Column(name = "source_organization", length = 120)
    private String sourceOrganization;

    @Column(name = "source_document", length = 255)
    private String sourceDocument;

    @Column(name = "source_url", length = 255)
    private String sourceUrl;

    @Column(name = "applicable_year", length = 10)
    private String applicableYear;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public ReferenceStandard() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplianceCategory() { return applianceCategory; }
    public void setApplianceCategory(String applianceCategory) { this.applianceCategory = applianceCategory; }

    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Double getReferenceValue() { return referenceValue; }
    public void setReferenceValue(Double referenceValue) { this.referenceValue = referenceValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getReferenceHoursPerDay() { return referenceHoursPerDay; }
    public void setReferenceHoursPerDay(Double referenceHoursPerDay) { this.referenceHoursPerDay = referenceHoursPerDay; }

    public String getSourceOrganization() { return sourceOrganization; }
    public void setSourceOrganization(String sourceOrganization) { this.sourceOrganization = sourceOrganization; }

    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getApplicableYear() { return applicableYear; }
    public void setApplicableYear(String applicableYear) { this.applicableYear = applicableYear; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
