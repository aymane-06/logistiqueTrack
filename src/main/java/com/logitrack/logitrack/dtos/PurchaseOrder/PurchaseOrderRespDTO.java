package com.logitrack.logitrack.dtos.PurchaseOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.OrderWarehouseRespDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseOrderRespDTO {
    public UUID id;
    public SupplierDTO supplier;
    public OrderWarehouseRespDTO warehouse;
    public String status;
    public LocalDateTime expectedDelivery;
    public List<PurchaseOrderLineRespDTO> lines;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
