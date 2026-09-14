package com.energyassessment.dto;

public class ReferenceStandardResponse {

    private Long id;
    private String applianceCategory;
    private String applianceName;
    private String referenceType;
    private Double referenceValue;
    private String unit;
    private Double referenceHoursPerDay;
    private String sourceOrganization;
    private String sourceDocument;
    private String sourceUrl;
    private String applicableYear;
    private String notes;

    public ReferenceStandardResponse() {
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
}
