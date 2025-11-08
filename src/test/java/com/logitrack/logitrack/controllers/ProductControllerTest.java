package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.services.ProductServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductServices productServices;

    private ProductDTO productDTO;
    private ProductRespDTO productRespDTO;
    private UUID productId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productServices))
                .build();
        objectMapper = new ObjectMapper();

        productId = UUID.randomUUID();

        productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
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
    @DisplayName("Should add product successfully")
    void testAddProduct() throws Exception {
        // Arrange
        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(productRespDTO);

        // Act & Assert
        ResultActions response = mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productServices).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should retrieve all products")
    void testGetAllProducts() throws Exception {
        // Arrange
        ProductRespDTO product2 = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .sku("SKU-002")
                .boughtPrice(new BigDecimal("30.00"))
                .active(true)
                .build();

        when(productServices.getAllProducts())
                .thenReturn(List.of(productRespDTO, product2));

        // Act & Assert
        ResultActions response = mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].sku").value("SKU-001"))
                .andExpect(jsonPath("$[1].sku").value("SKU-002"));

        verify(productServices).getAllProducts();
    }

    @Test
    @DisplayName("Should get product by SKU successfully")
    void testGetProductBySku() throws Exception {
        // Arrange
        String sku = "SKU-001";
        when(productServices.getProductBySku(sku))
                .thenReturn(productRespDTO);

        // Act & Assert
        ResultActions response = mockMvc.perform(get("/api/products/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-001"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productServices).getProductBySku(sku);
    }

    @Test
    @DisplayName("Should return error when product not found")
    void testGetProductBySku_NotFound() throws Exception {
        // Arrange
        String sku = "INVALID";
        when(productServices.getProductBySku(sku))
                .thenThrow(new IllegalArgumentException("Product not found with SKU: " + sku));

        // Act & Assert - Expect a 5xx error or servlet error due to unhandled exception
        mockMvc.perform(get("/api/products/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(productServices).getProductBySku(sku);
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() throws Exception {
        // Arrange
        String sku = "SKU-001";
        ProductRespDTO updatedProduct = ProductRespDTO.builder()
                .id(productId)
                .name("Updated Product")
                .sku("SKU-001")
                .boughtPrice(new BigDecimal("75.00"))
                .active(true)
                .build();

        when(productServices.updateProduct(eq(sku), any(ProductDTO.class)))
                .thenReturn(updatedProduct);

        // Act & Assert
        ResultActions response = mockMvc.perform(put("/api/products/update/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.boughtPrice").value(75.00));

        verify(productServices).updateProduct(eq(sku), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct() throws Exception {
        // Arrange
        String sku = "SKU-001";
        doNothing().when(productServices).deleteProductBySku(sku);

        // Act & Assert
        ResultActions response = mockMvc.perform(delete("/api/products/delete/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Product with SKU: " + sku + " has been deleted."));

        verify(productServices).deleteProductBySku(sku);
    }

    @Test
    @DisplayName("Should fail validation when required fields are missing")
    void testAddProduct_ValidationError() throws Exception {
        // Arrange - Empty DTO missing required name
        ProductDTO invalidDTO = ProductDTO.builder()
                .category("Electronics")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
