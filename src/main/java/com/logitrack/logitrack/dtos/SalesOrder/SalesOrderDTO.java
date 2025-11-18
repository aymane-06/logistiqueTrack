
package com.logitrack.logitrack.dtos.SalesOrder;

import java.util.List;
import java.util.UUID;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderLine.SalesOrderLineDTO;
import com.logitrack.logitrack.models.ENUM.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderDTO {

    @NotNull(message = "Client ID is required")
    private UUID clientId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Sales order lines are required")
    private List<SalesOrderLineDTO> lines;

    private OrderStatus status;

}
