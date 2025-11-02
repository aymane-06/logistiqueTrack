package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.*;
import com.logitrack.logitrack.repositories.ProductRepository;
import com.logitrack.logitrack.repositories.SupplierRepository;
import com.logitrack.logitrack.repositories.WarehouseManagerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

// PurchaseOrderMapper.java
@Mapper(componentModel = "spring")
public abstract class PurchaseOrderMapper {
    @Autowired
    protected  PurchaseOrderLineMapper purchaseOrderLineMapper;
    @Autowired
    protected  SupplierRepository supplierRepository;
    @Autowired
    protected  WarehouseManagerRepository warehouseManagerRepository;
    @Autowired
    protected  ProductRepository productRepository;


    public abstract PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    public  PurchaseOrder toEntity(PurchaseOrderDTO purchaseOrderDTO){
        if ( purchaseOrderDTO == null ) {
            return null;
        }
        Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + purchaseOrderDTO.getSupplierId() + " not found."));
        WAREHOUSE_MANAGER warehouseManager = warehouseManagerRepository.findById(purchaseOrderDTO.getWarehouseManagerId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse Manager with id " + purchaseOrderDTO.getWarehouseManagerId() + " not found."));

       PurchaseOrder purchaseOrder = PurchaseOrder.builder()
               .supplier(supplier)
               .warehouseManager(warehouseManager)
               .expectedDelivery(purchaseOrderDTO.getExpectedDelivery())
               .status(purchaseOrderDTO.getStatus())
               .build();
       purchaseOrderDTO.getLines().forEach(l-> {
           Product product = productRepository.findByIdAndActive(l.getProductId(),true).orElseThrow(()-> new IllegalArgumentException("Product with id " + l.getProductId() + " not found."));
           PurchaseOrderLine line = PurchaseOrderLine.builder()
                   .product(product)
                   .quantity(l.getQuantity())
                   .unitPrice(l.getUnitPrice())
                   .purchaseOrder(purchaseOrder)
                   .build();
           purchaseOrder.getLines().add(line);
         });
        return purchaseOrder;
    }
    public abstract void updatePurchaseOrderFromDto(PurchaseOrderDTO dto, @MappingTarget PurchaseOrder entity);
    public abstract PurchaseOrderRespDTO toResponseDTO(PurchaseOrder purchaseOrder);
}

