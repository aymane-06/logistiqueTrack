package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CarrierDTO {
    private UUID id;

    @NotBlank(message = "Carrier name is required")
    @Size(max = 255, message = "Carrier name must be at most 255 characters")
    private String name;

    @Email(message = "Contact email should be valid")
    private String contactEmail;

    private String contactPhone;

    private String baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private String cutOffTime;
    private String status;
}
