package com.energyassessment.dto;

/** Transparency metadata attached to every appliance result, per the project brief. */
public class ReferenceSourceDto {

    private String referenceType;      // GOVERNMENT_MANDATED | BEE_BENCHMARK | STAR_LABEL_INFO | PROJECT_REFERENCE
    private String sourceOrganization;
    private String sourceDocument;
    private String sourceUrl;
    private String applicableYear;
    private String notes;

    public ReferenceSourceDto() {
    }

    public ReferenceSourceDto(String referenceType, String sourceOrganization, String sourceDocument,
                               String sourceUrl, String applicableYear, String notes) {
        this.referenceType = referenceType;
        this.sourceOrganization = sourceOrganization;
        this.sourceDocument = sourceDocument;
        this.sourceUrl = sourceUrl;
        this.applicableYear = applicableYear;
        this.notes = notes;
    }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

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
