package com.logitrack.logitrack.dtos.Inventory;

import com.logitrack.logitrack.dtos.Product.ProductRespDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class WareHouseInventoryRespDTO {

        private String id;

        @NotNull(message = "Product ID is required")
        private ProductRespDTO product;

        @Min(value = 0, message = "Quantity on hand must be at least 0")
        private Integer qtyOnHand;

        @Min(value = 0, message = "Quantity reserved must be at least 0")
        private Integer qtyReserved;

        private String createdAt;

        private String updatedAt;


}
