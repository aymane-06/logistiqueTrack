package com.logitrack.logitrack.dtos.PurchaseOrder;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineRespDTO;
import com.logitrack.logitrack.dtos.SupplierDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderRespDTO {
    private String id;
    private SupplierDTO supplier;
    private String status;
    private LocalDateTime expectedDelivery;
    private List<PurchaseOrderLineRespDTO> lines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
