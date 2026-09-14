package com.energyassessment.dto;

import jakarta.validation.constraints.*;

public class ApplianceRequest {

    @NotBlank(message = "Appliance name is required")
    private String applianceName;

    private String category;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Power rating is required")
    @DecimalMin(value = "0.01", message = "Power rating must be greater than 0")
    private Double powerWatts;

    @NotNull(message = "Hours per day is required")
    @DecimalMin(value = "0.0", message = "Operating hours must be between 0 and 24 hours")
    @DecimalMax(value = "24.0", message = "Operating hours must be between 0 and 24 hours")
    private Double hoursPerDay;

    public ApplianceRequest() {
    }

    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPowerWatts() { return powerWatts; }
    public void setPowerWatts(Double powerWatts) { this.powerWatts = powerWatts; }

    public Double getHoursPerDay() { return hoursPerDay; }
    public void setHoursPerDay(Double hoursPerDay) { this.hoursPerDay = hoursPerDay; }
}
