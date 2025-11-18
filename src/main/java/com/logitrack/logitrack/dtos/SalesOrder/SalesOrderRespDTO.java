package com.logitrack.logitrack.dtos.SalesOrder;

import java.time.LocalDateTime;
import java.util.List;

import com.logitrack.logitrack.dtos.ClientDTO;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderLine.SalesOrderLineRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.OrderWarehouseRespDTO;
import com.logitrack.logitrack.models.Shipment;
import com.logitrack.logitrack.models.ENUM.OrderStatus;

import lombok.Data;

@Data
public class SalesOrderRespDTO {
    private String id;
    private ClientDTO client;
    private OrderWarehouseRespDTO warehouse;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reservedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private List<SalesOrderLineRespDTO> lines;
    private Shipment shipment;
    private LocalDateTime updatedAt;

}
