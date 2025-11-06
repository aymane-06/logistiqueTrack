
package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalesOrderDTO {
    private String id;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Warehouse ID is required")
    private String warehouseId;

    @NotBlank(message = "Status is required")
    private String status;

    private String createdAt;
    private String reservedAt;
    private String shippedAt;
    private String deliveredAt;
    private String updatedAt;
}
