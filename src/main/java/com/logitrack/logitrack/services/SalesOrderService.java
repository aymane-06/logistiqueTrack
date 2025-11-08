package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;
import com.logitrack.logitrack.mapper.PurchaseOrderMapper;
import com.logitrack.logitrack.mapper.SalesOrderMapper;
import com.logitrack.logitrack.models.*;
import com.logitrack.logitrack.models.ENUM.MovementType;
import com.logitrack.logitrack.models.ENUM.OrderStatus;
import com.logitrack.logitrack.models.ENUM.ShipmentStatus;
import com.logitrack.logitrack.repositories.CarrierRepository;
import com.logitrack.logitrack.repositories.SalesOrderRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import static com.logitrack.logitrack.models.ENUM.MovementType.OUTBOUND;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final CarrierRepository carrierRepository;

    public SalesOrderRespDTO createSalesOrder(SalesOrderDTO salesOrderDTO) {
        SalesOrder salesOrder=salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder.setStatus(OrderStatus.CREATED);

        salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public List<SalesOrderRespDTO> getAllSalesOrders() {
        List<SalesOrder> salesOrders = salesOrderRepository.findAll();
        return salesOrders.stream()
                .map(salesOrderMapper::toRespDTO)
                .toList();
    }

    public SalesOrderRespDTO getSalesOrderById(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public SalesOrderRespDTO updateSalesOrder(UUID id, SalesOrderDTO salesOrderDTO) {
        SalesOrder existingOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));
        if(existingOrder.getStatus() == OrderStatus.RESERVED || existingOrder.getStatus() == OrderStatus.SHIPPED){
            throw new IllegalStateException("Reserved or shipped orders cannot be updated.");
        }
        salesOrderMapper.updateSalesOrderFromDto(salesOrderDTO, existingOrder);
        salesOrderRepository.save(existingOrder);
        return salesOrderMapper.toRespDTO(existingOrder);
    }

    public SalesOrderRespDTO deleteSalesOrderById(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));
        salesOrderRepository.delete(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public Object reserveSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));

        if(salesOrder.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only orders in CREATED status can be reserved.");
        }
        List<PurchaseOrderLine> linesToPurchase = new ArrayList<PurchaseOrderLine>();
        
        // Process ALL lines (both backorder and non-backorder)
        for (var line : salesOrder.getLines()) {
            if (line.getBackorder()) {
                // For backorder lines: transfer from other warehouses
                Integer remainingQty = transferProductsBetweenWarehouses(salesOrder.getWarehouse(), line.getProduct().getId(), line.getQuantity());
                if (remainingQty == 0) {
                    line.setBackorder(false);
                } else {
                    PurchaseOrderLine lineToPurchase = PurchaseOrderLine.builder()
                            .product(line.getProduct())
                            .quantity(remainingQty)
                            .unitPrice(line.getUnitPrice())
                            .build();
                    linesToPurchase.add(lineToPurchase);
                }
            } else {
                // For non-backorder lines: reserve from current warehouse
                reserveInventoryFromWarehouse(salesOrder.getWarehouse(), line.getProduct().getId(), line.getQuantity());
            }
        }
        
        // Process purchase order lines if any
        if (!linesToPurchase.isEmpty()) {
            PurchaseOrder newPurchaseOrder = PurchaseOrder.builder()
                    .warehouse(salesOrder.getWarehouse())
                    .lines(linesToPurchase)
                    .status(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.CREATED)
                    .expectedDelivery(LocalDateTime.now().plusDays(7))
                    .build();
                    return Map.of(
                        "message", "Purchase order created for backordered lines",
                        "purchaseOrder", purchaseOrderMapper.toResponseDTO(newPurchaseOrder)
                    );
        }
        // Update the sales order status to RESERVED
        salesOrder.setStatus(OrderStatus.RESERVED);
        salesOrder.setReservedAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }


    private Integer transferProductsBetweenWarehouses(Warehouse destination, UUID productId, Integer quantity) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        for (Warehouse source : warehouses) {
            if (source.getId().equals(destination.getId())) {
                continue;
            }
        
            List<Inventory> sourceInventories = source.getInventories();
            for (Inventory inv : sourceInventories) {
                if (inv.getProduct().getId().equals(productId) && inv.getQtyOnHand() > 0) {
                    int availableQty = inv.getQtyOnHand()- inv.getQtyReserved();
                    Integer qtyToTransfer = Math.min(availableQty, quantity);

                    // Deduct from source inventory
                    inv.setQtyOnHand(availableQty - qtyToTransfer);
                    InventoryMovement inventoryMovement = InventoryMovement.builder()
                            .inventory(inv)
                            .type(OUTBOUND)
                            .quantity(qtyToTransfer)
                            .occurredAt(LocalDateTime.now())
                            .build();
                    inv.getInventoryMovements().add(inventoryMovement);

                    // Save source warehouse changes (cascades to inventory and movements)
                    warehouseRepository.save(source);

                    // Add to destination inventory
                    com.logitrack.logitrack.models.Inventory destInv = destination.getInventories().stream()
                            .filter(i -> i.getProduct().getId().equals(productId))
                            .findFirst()
                            .orElseGet(() -> {
                                Inventory newInv = Inventory.builder()
                                        .product(inv.getProduct())
                                        .qtyOnHand(0)
                                        .qtyReserved(0)
                                        .warehouse(destination)
                                        .build();
                                destination.getInventories().add(newInv);
                                InventoryMovement inboundMovement = InventoryMovement.builder()
                                        .inventory(newInv)
                                        .type(MovementType.INBOUND)
                                        .quantity(0)
                                        .occurredAt(LocalDateTime.now())
                                        .build();
                                newInv.getInventoryMovements().add(inboundMovement);
                                return newInv;
                            });
                    destInv.setQtyOnHand(destInv.getQtyOnHand() + qtyToTransfer);
                    destInv.setQtyReserved(destInv.getQtyReserved() + qtyToTransfer);

                    // Save destination warehouse changes (cascades to inventory)
                    warehouseRepository.save(destination);

                    quantity -= qtyToTransfer;
                    if (quantity == 0) {
                        return quantity; // Transfer complete
                    }
                }
            }
        }
        return quantity ; // Return remaining quantity that couldn't be transferred
    
    }

    private void reserveInventoryFromWarehouse(Warehouse warehouse, UUID productId, Integer quantity) {
        List<Inventory> inventories = warehouse.getInventories();
        for (Inventory inv : inventories) {
            if (inv.getProduct().getId().equals(productId)) {
                int availableQty = inv.getQtyOnHand() - inv.getQtyReserved();
                if (availableQty >= quantity) {
                    inv.setQtyReserved(inv.getQtyReserved() + quantity);
                    warehouseRepository.save(warehouse);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Insufficient inventory for product " + productId);
    }


    public SalesOrderRespDTO shipSalesOrder(UUID id,UUID carrierId) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));

        if(salesOrder.getStatus() != OrderStatus.RESERVED) {
            throw new IllegalStateException("Only orders in RESERVED status can be shipped.");
        }

        // Deduct reserved quantities from inventory
        for (SalesOrderLine line : salesOrder.getLines()) {
            List<Inventory> inventories = salesOrder.getWarehouse().getInventories();
            for (Inventory inv : inventories) {
                if (inv.getProduct().getId().equals(line.getProduct().getId())) {
                    inv.setQtyOnHand(inv.getQtyOnHand() - line.getQuantity());
                    inv.setQtyReserved(inv.getQtyReserved() - line.getQuantity());

                    InventoryMovement inventoryMovement = InventoryMovement.builder()
                            .inventory(inv)
                            .type(OUTBOUND)
                            .quantity(line.getQuantity())
                            .occurredAt(LocalDateTime.now())
                            .build();
                    inv.getInventoryMovements().add(inventoryMovement);

                    warehouseRepository.save(salesOrder.getWarehouse());
                    break;
                }
            }
        }
        Carrier carrier = carrierRepository.findById(carrierId).orElseThrow(()-> new IllegalArgumentException("Carrier with id " + carrierId + " not found."));
        if(carrier.getMaxDailyCapacity()-carrier.getCurrentDailyShipments()<1){
            throw new IllegalStateException("Carrier has reached its maximum daily capacity.");
        }
        carrier.setCurrentDailyShipments(carrier.getCurrentDailyShipments()+1);
        carrierRepository.save(carrier);

        // Create shipment record
        Shipment shipment = Shipment.builder()
                .carrier(carrier)
                .salesOrder(salesOrder)
                .status(ShipmentStatus.PLANNED)
                .isCutOffPassed(LocalTime.now().isAfter(carrier.getCutOffTime()))
                .shippedDate(LocalTime.now().isAfter(carrier.getCutOffTime())? LocalDateTime.now().plusDays(1) : LocalDateTime.now())
                .build();
        salesOrder.setShipment(shipment);


        // Update sales order status to SHIPPED
        salesOrder.setStatus(OrderStatus.SHIPPED);
        salesOrder.setShippedAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public SalesOrderRespDTO deliverSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));

        if(salesOrder.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only orders in SHIPPED status can be delivered.");
        }

        // Update sales order status to DELIVERED
        salesOrder.getShipment().setStatus(com.logitrack.logitrack.models.ENUM.ShipmentStatus.DELIVERED);
        salesOrder.getShipment().setDeliveredDate(LocalDateTime.now());
        salesOrder.setStatus(OrderStatus.DELIVERED);
        salesOrder.setDeliveredAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public SalesOrderRespDTO cancelSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order with id " + id + " not found."));

        if(salesOrder.getStatus() == OrderStatus.SHIPPED || salesOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Shipped or delivered orders cannot be canceled.");
        }

        // Release reserved quantities back to inventory
        if(salesOrder.getStatus() == OrderStatus.RESERVED) {
            for (SalesOrderLine line : salesOrder.getLines()) {
                List<Inventory> inventories = salesOrder.getWarehouse().getInventories();
                for (Inventory inv : inventories) {
                    if (inv.getProduct().getId().equals(line.getProduct().getId())) {
                        inv.setQtyReserved(inv.getQtyReserved() - line.getQuantity());
                        warehouseRepository.save(salesOrder.getWarehouse());
                        break;
                    }
                }
            }
        }

        // Update sales order status to CANCELED
        salesOrder.setStatus(OrderStatus.CANCELED);
        salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toRespDTO(salesOrder);
    }
}
