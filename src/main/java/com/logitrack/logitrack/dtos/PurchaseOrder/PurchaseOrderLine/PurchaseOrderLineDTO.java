
package com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PurchaseOrderLineDTO {

    @NotBlank(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    @PositiveOrZero(message = "Unit price must be zero or positive")
    private BigDecimal unitPrice;

}
