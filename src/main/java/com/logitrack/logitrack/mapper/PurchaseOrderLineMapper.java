package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineDTO;
import com.logitrack.logitrack.models.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {
    PurchaseOrderLineDTO toDTO(PurchaseOrderLine purchaseOrderLine);
    PurchaseOrderLine toEntity(PurchaseOrderLineDTO purchaseOrderLineDTO);
    void updatePurchaseOrderLineFromDto(PurchaseOrderLineDTO dto, @MappingTarget PurchaseOrderLine entity);
}
