package com.energyassessment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class AssessmentRequest {

    @NotNull(message = "Household size is required")
    @Min(value = 1, message = "Household size must be at least 1")
    @Max(value = 20, message = "Household size must be 20 or fewer")
    private Integer householdSize;

    private String houseType;

    // Optional resident-reported bill, used only for comparison.
    @DecimalMin(value = "0.0", message = "Actual bill must be a positive amount")
    private Double actualBill;

    // Optional — falls back to the server-configured default tariff if omitted.
    @DecimalMin(value = "0.01", message = "Tariff must be greater than 0")
    private Double tariff;

    @NotEmpty(message = "Please add at least one appliance before continuing.")
    @Valid
    private List<ApplianceRequest> appliances;

    public AssessmentRequest() {
    }

    public Integer getHouseholdSize() { return householdSize; }
    public void setHouseholdSize(Integer householdSize) { this.householdSize = householdSize; }

    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }

    public Double getActualBill() { return actualBill; }
    public void setActualBill(Double actualBill) { this.actualBill = actualBill; }

    public Double getTariff() { return tariff; }
    public void setTariff(Double tariff) { this.tariff = tariff; }

    public List<ApplianceRequest> getAppliances() { return appliances; }
    public void setAppliances(List<ApplianceRequest> appliances) { this.appliances = appliances; }
}
