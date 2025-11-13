package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.exception.ResourceNotFoundException;
import com.logitrack.logitrack.mapper.ProductMapper;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServices Unit Tests")
class ProductServicesTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServices productServices;

    private ProductDTO productDTO;
    private Product product;
    private ProductRespDTO productRespDTO;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        // Initialize test data
        productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .sku("SKU-001")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRespDTO = ProductRespDTO.builder()
                .id(productId)
                .name("Test Product")
                .sku("SKU-001")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        // Arrange
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(productRespDTO);

        // Act
        ProductRespDTO result = productServices.saveProduct(productDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("SKU-001");
        assertThat(result.getName()).isEqualTo("Test Product");
        
        // Verify interactions
        verify(productMapper).toEntity(productDTO);
        verify(productRepository).save(product);
        verify(productMapper).toResponseDTO(product);
    }

    @Test
    @DisplayName("Should retrieve all products")
    void testGetAllProducts() {
        // Arrange
        Product product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .sku("SKU-002")
                .active(true)
                .build();
        
        ProductRespDTO productRespDTO2 = ProductRespDTO.builder()
                .id(product2.getId())
                .name("Product 2")
                .sku("SKU-002")
                .active(true)
                .build();
        
        List<Product> products = List.of(product, product2);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponseDTO(product)).thenReturn(productRespDTO);
        when(productMapper.toResponseDTO(product2)).thenReturn(productRespDTO2);

        // Act
        List<ProductRespDTO> result = productServices.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should get product by SKU successfully")
    void testGetProductBySku() {
        // Arrange
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(productRespDTO);

        // Act
        ProductRespDTO result = productServices.getProductBySku("SKU-001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("SKU-001");
        verify(productRepository).findBySku("SKU-001");
    }

    @Test
    @DisplayName("Should throw exception when product not found by SKU")
    void testGetProductBySku_NotFound() {
        // Arrange
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productServices.getProductBySku("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found with SKU: INVALID");
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() {
        // Arrange
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(productRespDTO);

        // Act
        ProductRespDTO result = productServices.updateProduct("SKU-001", productDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(productRepository).findBySku("SKU-001");
        verify(productMapper).updateProductFromDto(productDTO, product);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productServices.updateProduct("INVALID", productDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with SKU: INVALID");
    }

    @Test
    @DisplayName("Should delete product by SKU successfully")
    void testDeleteProductBySku() {
        // Arrange
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));

        // Act
        productServices.deleteProductBySku("SKU-001");

        // Assert
        verify(productRepository).findBySku("SKU-001");
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDeleteProductBySku_NotFound() {
        // Arrange
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productServices.deleteProductBySku("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found with SKU: INVALID");
    }
}
