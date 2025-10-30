
package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderLineDTO {
    private String id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Sales order ID is required")
    private String salesOrderId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String unitPrice;
    private Boolean backorder;
    private String createdAt;
    private String updatedAt;
}
