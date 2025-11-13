package com.logitrack.logitrack.dtos.PurchaseOrder;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineRespDTO;
import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.dtos.Warehouse.OrderWarehouseRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
