package com.logitrack.logitrack.dtos;

import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryDTO {
    private String id;

    @NotNull(message = "Product ID is required")
    private ProductRespDTO product;

    @NotNull(message = "Warehouse ID is required")
    private WarehouseRespDTO warehouse;

    @Min(value = 0, message = "Quantity on hand must be at least 0")
    private Integer qtyOnHand;

    @Min(value = 0, message = "Quantity reserved must be at least 0")
    private Integer qtyReserved;

    private String createdAt;

    private String updatedAt;
}
