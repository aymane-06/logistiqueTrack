package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryMovementDTO {
    private String id;

    @NotNull(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Warehouse ID is required")
    private String warehouseId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Movement type is required")
    private String movementType;

    private String occurredAt;
    private String referenceDocument;
    private String description;
    private String createdAt;
    private String updatedAt;
}
