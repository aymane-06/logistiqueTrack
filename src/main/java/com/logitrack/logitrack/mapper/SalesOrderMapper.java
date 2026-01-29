package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;
import com.logitrack.logitrack.models.*;
import com.logitrack.logitrack.repositories.ClientRepository;
import com.logitrack.logitrack.repositories.ProductRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;
import com.logitrack.logitrack.services.InventoryService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mapper(componentModel = "spring" ,uses = {ShipmentMapper.class})
public abstract class SalesOrderMapper {

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected WarehouseRepository warehouseRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected InventoryService inventoryService;


    public abstract SalesOrderDTO toDTO(SalesOrder salesOrder);

    public SalesOrder toEntity(SalesOrderDTO salesOrderDTO){
        if ( salesOrderDTO == null ) {
            return null;
        }
        Client client = clientRepository.findById(salesOrderDTO.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client with id " + salesOrderDTO.getClientId() + " not found."));

        Warehouse warehouse = warehouseRepository.findById(salesOrderDTO.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse with id " + salesOrderDTO.getWarehouseId() + " not found."));

        List<Inventory> warehouseInventories = warehouse.getInventories();
        if(warehouseInventories.isEmpty()){
            throw new IllegalArgumentException("No inventories found for warehouse with id " + warehouse.getId());
        }
            warehouseInventories.forEach(i->{
                salesOrderDTO.getLines().forEach(l->{
                    if(i.getProduct().getId().equals(l.getProductId())){
                        if(i.getQtyOnHand() - i.getQtyReserved() < l.getQuantity()){
                            List<Warehouse> warehouses = warehouseRepository.findAll();
                            Integer totalAvailable = warehouses.stream().mapToInt(w -> {
                                AtomicReference<Integer> availableQty = new AtomicReference<>(0);
                                w.getInventories().forEach(inv -> {
                                    if (inv.getProduct().getId().equals(l.getProductId())) {
                                        availableQty.updateAndGet(v -> v + (inv.getQtyOnHand() - inv.getQtyReserved()));
                                    }
                                });
                                return availableQty.get();
                            }).sum();
                            if(totalAvailable < l.getQuantity()){
                                
                                l.setBackorder(true);
                            }
                            else {
                                l.setBackorder(false);
                            }
                        } else {
                            l.setBackorder(false);
                        }

                    }else throw new IllegalArgumentException("Product with id " + l.getProductId() + " not found in warehouse inventories.");
                });

            });

        SalesOrder salesOrder = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .build();
        salesOrderDTO.getLines().forEach(l-> {
            Product product = productRepository.findById(l.getProductId()).orElseThrow(()-> new IllegalArgumentException("Product with id " + l.getProductId() + " not found."));
            SalesOrderLine line = SalesOrderLine.builder()
                    .product(product)
                    .quantity(l.getQuantity())
                    .unitPrice(l.getUnitPrice())
                    .salesOrder(salesOrder)
                    .backorder(l.getBackorder())
                    .build();
            salesOrder.getLines().add(line);
        });

        return salesOrder;
    }

    public void updateSalesOrderFromDto(SalesOrderDTO dto, @MappingTarget SalesOrder entity) {
        if ( dto == null ) {
            return;
        }

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client with id " + dto.getClientId() + " not found."));
            entity.setClient(client);
        }

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Warehouse with id " + dto.getWarehouseId() + " not found."));
            entity.setWarehouse(warehouse);
        }

        if (dto.getLines() != null) {
            entity.getLines().clear();
            dto.getLines().forEach(l -> {
                Product product = productRepository.findById(l.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product with id " + l.getProductId() + " not found."));
                SalesOrderLine line = SalesOrderLine.builder()
                        .product(product)
                        .quantity(l.getQuantity())
                        .unitPrice(l.getUnitPrice())
                        .salesOrder(entity)
                        .backorder(l.getBackorder())
                        .build();
                entity.getLines().add(line);
            });
        }
    }

    public abstract SalesOrderRespDTO toRespDTO(SalesOrder salesOrder);
}
