package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.*;
import com.logitrack.logitrack.repositories.ProductRepository;
import com.logitrack.logitrack.repositories.SupplierRepository;
import com.logitrack.logitrack.repositories.WarehouseManagerRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
@Setter
public abstract class PurchaseOrderMapper {
    @Autowired
    protected  SupplierRepository supplierRepository;
    @Setter
    @Autowired
    protected  ProductRepository productRepository;
    @Autowired
    protected WarehouseRepository warehouseRepository;


    public abstract PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    public  PurchaseOrder toEntity(PurchaseOrderDTO purchaseOrderDTO){
        if ( purchaseOrderDTO == null ) {
            return null;
        }
        Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + purchaseOrderDTO.getSupplierId() + " not found."));

        Warehouse warehouse = warehouseRepository.findById(purchaseOrderDTO.getWarehouseId()).orElseThrow(()-> new IllegalArgumentException("Warehouse with id " + purchaseOrderDTO.getWarehouseId() + " not found."));

       PurchaseOrder purchaseOrder = PurchaseOrder.builder()
               .supplier(supplier)
               .warehouse(warehouse)
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
    public void updatePurchaseOrderFromDto(PurchaseOrderDTO dto, @MappingTarget PurchaseOrder entity){
        if ( dto == null ) {
            return;
        }
        if (dto.getExpectedDelivery() != null) {
            entity.setExpectedDelivery( dto.getExpectedDelivery() );
        }
        if (dto.getStatus() != null) {
            entity.setStatus( dto.getStatus() );
        }
        if(dto.getSupplierId()!=null){
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + dto.getSupplierId() + " not found."));
            entity.setSupplier(supplier);
        }
        if(dto.getWarehouseId()!=null){
            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId()).orElseThrow(()-> new IllegalArgumentException("Warehouse with id " + dto.getWarehouseId() + " not found."));

            entity.setWarehouse(warehouse);
        }
        if(dto.getLines()!=null) {
            entity.getLines().clear();
            dto.getLines().forEach(l -> {
                Product product = productRepository.findByIdAndActive(l.getProductId(), true).orElseThrow(() -> new IllegalArgumentException("Product with id " + l.getProductId() + " not found."));
                PurchaseOrderLine line = PurchaseOrderLine.builder()
                        .product(product)
                        .quantity(l.getQuantity())
                        .unitPrice(l.getUnitPrice())
                        .purchaseOrder(entity)
                        .build();
                entity.getLines().add(line);
            });
        }
    }
    public abstract PurchaseOrderRespDTO toResponseDTO(PurchaseOrder purchaseOrder);

}

