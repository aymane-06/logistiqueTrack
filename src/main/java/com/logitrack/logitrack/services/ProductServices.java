package com.logitrack.logitrack.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.exception.BusinessException;
import com.logitrack.logitrack.exception.ResourceNotFoundException;
import com.logitrack.logitrack.mapper.ProductMapper;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.models.ENUM.OrderStatus;
import com.logitrack.logitrack.repositories.ProductRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServices {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductRespDTO saveProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    public List<ProductRespDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public ProductRespDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
        return productMapper.toResponseDTO(product);
    }

    public ProductRespDTO updateProduct(String sku, @Valid ProductDTO productDTO) {
        Product existingProduct = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        productMapper.updateProductFromDto(productDTO, existingProduct);
        productRepository.save(existingProduct);
        return productMapper.toResponseDTO(existingProduct);

    }

    public void deleteProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
        productRepository.delete(product);
    }

    public Product productStatusUpdate(String sku, boolean status) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product with SKU " + sku + " not found."));

        if(!status){
            boolean hasActiveSalesOrders = product.getSalesOrderLines().stream()
                    .anyMatch(line -> line.getSalesOrder().getStatus() == OrderStatus.CREATED || line.getSalesOrder().getStatus() == OrderStatus.RESERVED);
            if (hasActiveSalesOrders) {
                throw new BusinessException("Cannot deactivate product. It is associated with active sales orders.");
            }

            boolean hasAvailableInventory = product.getInventory().stream()
                    .anyMatch(inventory -> inventory.getQtyOnHand() > 0);
            if (hasAvailableInventory) {
                throw new BusinessException("Cannot deactivate product. It has available inventory.");
            }
        }
         product.setActive(status);
        productRepository.save(product);
        return product;
    }



}
