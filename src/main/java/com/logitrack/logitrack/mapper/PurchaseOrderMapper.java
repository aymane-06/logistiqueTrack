package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.PurchaseOrderDTO;
import com.logitrack.logitrack.models.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    PurchaseOrder toEntity(PurchaseOrderDTO purchaseOrderDTO);
    void updatePurchaseOrderFromDto(PurchaseOrderDTO dto, @MappingTarget PurchaseOrder entity);
}
