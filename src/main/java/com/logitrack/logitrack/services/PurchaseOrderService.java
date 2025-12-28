package com.logitrack.logitrack.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logitrack.logitrack.audit.BusinessAuditService;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.mapper.PurchaseOrderMapper;
import com.logitrack.logitrack.models.Inventory;
import com.logitrack.logitrack.models.InventoryMovement;
import com.logitrack.logitrack.models.PurchaseOrder;
import com.logitrack.logitrack.models.ENUM.MovementType;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.repositories.PurchaseOrderRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final BusinessAuditService businessAuditService;


    public PurchaseOrderRespDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
            PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
            purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
        purchaseOrderRepository.save(purchaseOrder);
        
        // Log business audit event
        businessAuditService.logPurchaseOrderCreated(
            purchaseOrder.getId(),
            purchaseOrder.getSupplier().getId(),
            purchaseOrder.getWarehouse().getId(),
            purchaseOrder.getLines().size()
        );
        
            return purchaseOrderMapper.toResponseDTO(purchaseOrder);
    }

    public List<PurchaseOrderRespDTO> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        return purchaseOrders.stream()
                .map(purchaseOrderMapper::toResponseDTO)
                .toList();
    }

    public PurchaseOrderRespDTO getPurchaseOrderById(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order with id " + id + " not found."));
        return purchaseOrderMapper.toResponseDTO(purchaseOrder);
    }

    public PurchaseOrderRespDTO updatePurchaseOrder(UUID id, @Valid PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder existingPurchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order with id " + id + " not found."));
        purchaseOrderMapper.updatePurchaseOrderFromDto(purchaseOrderDTO, existingPurchaseOrder);
        purchaseOrderRepository.save(existingPurchaseOrder);
        return purchaseOrderMapper.toResponseDTO(existingPurchaseOrder);
    }

    public PurchaseOrderRespDTO deletePurchaseOrderById(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order with id " + id + " not found."));
        purchaseOrderRepository.delete(purchaseOrder);
        return purchaseOrderMapper.toResponseDTO(purchaseOrder);
    }

    public PurchaseOrderRespDTO parchaseOrderStatusUpdate(UUID id, PurchaseOrderStatus status) {
        PurchaseOrder existingPurchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order with id " + id + " not found."));
        if(existingPurchaseOrder.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new IllegalArgumentException("Cannot update status of a delivered purchase order.");
        }
        
        PurchaseOrderStatus oldStatus = existingPurchaseOrder.getStatus();
        existingPurchaseOrder.setStatus(status);
        
        // Log business audit event
        businessAuditService.logPurchaseOrderStatusChange(
            existingPurchaseOrder.getId(),
            oldStatus.toString(),
            status.toString()
        );
        
        if(status == PurchaseOrderStatus.RECEIVED) {
            existingPurchaseOrder.setActualDelivery(LocalDateTime.now());

            existingPurchaseOrder.getLines().forEach(line -> {
                line.getProduct().setBoughtPrice(line.getUnitPrice());
                List<Inventory> inventories = existingPurchaseOrder.getWarehouse().getInventories();
                inventories.stream()
                        .filter(inv -> inv.getProduct().getId().equals(line.getProduct().getId()))
                        .findFirst()
                        .ifPresentOrElse(
                                inv -> {
                                    inv.setQtyOnHand(inv.getQtyOnHand() + line.getQuantity());
                                    InventoryMovement inventoryMovement = InventoryMovement.builder()
                                            .inventory(inv)
                                            .type(MovementType.INBOUND)
                                            .quantity(line.getQuantity())
                                            .occurredAt(LocalDateTime.now())
                                            .build();
                                    inv.getInventoryMovements().add(inventoryMovement);
                                },
                                () -> {
                                    Inventory newInventory = Inventory.builder()
                                            .product(line.getProduct())
                                            .qtyOnHand(line.getQuantity())
                                            .qtyReserved(0)
                                            .warehouse(existingPurchaseOrder.getWarehouse())
                                            .build();
                                    line.getProduct().getInventory().add(newInventory);
                                    // Add to warehouse inventories list - will be cascaded when saving PurchaseOrder
                                    existingPurchaseOrder.getWarehouse().getInventories().add(newInventory);


                                    InventoryMovement inventoryMovement = InventoryMovement.builder()
                                            .inventory(newInventory)
                                            .type(MovementType.INBOUND)
                                            .quantity(line.getQuantity())
                                            .occurredAt(LocalDateTime.now())
                                            .build();
                                    newInventory.getInventoryMovements().add(inventoryMovement);
                                }
                        );
            });


        }
        purchaseOrderRepository.save(existingPurchaseOrder);
        return purchaseOrderMapper.toResponseDTO(existingPurchaseOrder);
    }


}
