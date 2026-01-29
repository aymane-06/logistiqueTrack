
package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShipmentDTO {
    private String id;

    @NotBlank(message = "Sales order ID is required")
    private String salesOrderId;

    @NotBlank(message = "Carrier ID is required")
    private String carrierId;

    private String trackingNumber;

    @NotBlank(message = "Status is required")
    private String status;

    private String plannedDate;
    private String shippedDate;
    private String deliveredDate;
    private String shippingCost;
    private Boolean isCutOffPassed;
    private String createdAt;
    private String updatedAt;
}
