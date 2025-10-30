package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.ProductDTO;
import com.logitrack.logitrack.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);
    void updateProductFromDto(ProductDTO dto, @MappingTarget Product entity);
}
