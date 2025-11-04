package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.mapper.ProductMapper;
import com.logitrack.logitrack.models.Inventory;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
        productMapper.updateProductFromDto(productDTO, existingProduct);
        productRepository.save(existingProduct);
        return productMapper.toResponseDTO(existingProduct);

    }

    public void deleteProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
        productRepository.delete(product);
    }

}
