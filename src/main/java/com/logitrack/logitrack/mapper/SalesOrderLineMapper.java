package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderLine.SalesOrderLineDTO;
import com.logitrack.logitrack.models.SalesOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {
    SalesOrderLineDTO toDTO(SalesOrderLine salesOrderLine);
    SalesOrderLine toEntity(SalesOrderLineDTO salesOrderLineDTO);
    void updateSalesOrderLineFromDto(SalesOrderLineDTO dto, @MappingTarget SalesOrderLine entity);
}
