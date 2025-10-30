
package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PurchaseOrderDTO {
    private String id;

    @NotBlank(message = "Supplier ID is required")
    private String supplierId;

    @NotBlank(message = "Status is required")
    private String status;

    private String createdAt;
    private String updatedAt;

    @NotBlank(message = "Expected delivery is required")
    private String expectedDelivery;
}
