
package com.logitrack.logitrack.dtos.PurchaseOrder;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineDTO;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PurchaseOrderDTO {

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    @NotNull(message = "Supplier is required")
    private UUID supplierId;
    @NotNull(message = "Purchase order lines are required")
    private List<PurchaseOrderLineDTO> lines;
    @Future
    private LocalDateTime expectedDelivery;
    @Builder.Default
    private PurchaseOrderStatus status= PurchaseOrderStatus.CREATED;
}
