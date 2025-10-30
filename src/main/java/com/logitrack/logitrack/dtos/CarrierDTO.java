package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CarrierDTO {
    private String id;

    @NotBlank(message = "Carrier code is required")
    @Size(max = 50, message = "Carrier code must be at most 50 characters")
    private String code;

    @NotBlank(message = "Carrier name is required")
    @Size(max = 255, message = "Carrier name must be at most 255 characters")
    private String name;

    @Email(message = "Contact email should be valid")
    private String contactEmail;

    @Size(min = 14, max = 14, message = "Phone number must be in the format XXX-XXX-XXXX")
    private String contactPhone;

    private String baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private String cutOffTime;
    private String status;
    private String createdAt;
    private String updatedAt;
}
