
package com.logitrack.logitrack.dtos.SalesOrder;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderLine.SalesOrderLineDTO;
import com.logitrack.logitrack.models.ENUM.OrderStatus;
import com.logitrack.logitrack.models.SalesOrderLine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

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
