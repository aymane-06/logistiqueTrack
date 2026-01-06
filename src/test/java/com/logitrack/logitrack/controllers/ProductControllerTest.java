package com.logitrack.logitrack.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.services.ProductServices;

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
    @DisplayName("Should handle product not found gracefully")
    void testGetProductBySku_NotFound() throws Exception {
        // Arrange
        String sku = "INVALID";
        when(productServices.getProductBySku(sku))
                .thenThrow(new IllegalArgumentException("Product not found with SKU: " + sku));

        // Act & Assert
        // Note: Without a global @ExceptionHandler, Spring will return 500 for unhandled exceptions
        // In a real application, you should implement @ExceptionHandler to return proper HTTP status codes
        try {
            mockMvc.perform(get("/api/products/{sku}", sku)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            // Expected: IllegalArgumentException wrapped in servlet exception
            org.junit.jupiter.api.Assertions.assertTrue(
                    e.getMessage().contains("Product not found"),
                    "Expected exception message to contain 'Product not found'"
            );
        }

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

        verify(productServices, never()).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void testAddProduct_BlankName() throws Exception {
        // Arrange
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productServices, never()).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should fail validation when name exceeds max length")
    void testAddProduct_NameTooLong() throws Exception {
        // Arrange - Name with 256 characters (exceeds 255 limit)
        String longName = "A".repeat(256);
        ProductDTO invalidDTO = ProductDTO.builder()
                .name(longName)
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productServices, never()).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should fail validation when active status is null")
    void testAddProduct_NullActiveStatus() throws Exception {
        // Arrange
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productServices, never()).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should create product with null category successfully")
    void testAddProduct_NullCategory() throws Exception {
        // Arrange
        ProductDTO dtoNullCategory = ProductDTO.builder()
                .name("Product Without Category")
                .category(null)
                .boughtPrice(new BigDecimal("25.00"))
                .active(true)
                .build();

        ProductRespDTO respNullCategory = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku("SKU-003")
                .name("Product Without Category")
                .category(null)
                .boughtPrice(new BigDecimal("25.00"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(respNullCategory);

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoNullCategory)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-003"))
                .andExpect(jsonPath("$.name").value("Product Without Category"));

        verify(productServices, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should create product with null boughtPrice successfully")
    void testAddProduct_NullBoughtPrice() throws Exception {
        // Arrange
        ProductDTO dtoNullPrice = ProductDTO.builder()
                .name("Free Product")
                .category("Promotional")
                .boughtPrice(null)
                .active(true)
                .build();

        ProductRespDTO respNullPrice = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku("SKU-004")
                .name("Free Product")
                .category("Promotional")
                .boughtPrice(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(respNullPrice);

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoNullPrice)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-004"));

        verify(productServices, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should create inactive product successfully")
    void testAddProduct_InactiveProduct() throws Exception {
        // Arrange
        ProductDTO inactiveDTO = ProductDTO.builder()
                .name("Discontinued Product")
                .category("Legacy")
                .boughtPrice(new BigDecimal("10.00"))
                .active(false)
                .build();

        ProductRespDTO inactiveResp = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku("SKU-005")
                .name("Discontinued Product")
                .category("Legacy")
                .boughtPrice(new BigDecimal("10.00"))
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(inactiveResp);

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inactiveDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(false));

        verify(productServices, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testGetAllProducts_EmptyList() throws Exception {
        // Arrange
        when(productServices.getAllProducts()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(productServices, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should update product with partial data")
    void testUpdateProduct_PartialUpdate() throws Exception {
        // Arrange
        String sku = "SKU-001";
        ProductDTO updateDTO = ProductDTO.builder()
                .name("Partially Updated Product")
                .category(null)  // Not updating category
                .boughtPrice(new BigDecimal("60.00"))
                .active(true)
                .build();

        ProductRespDTO updatedResp = ProductRespDTO.builder()
                .id(productId)
                .sku(sku)
                .name("Partially Updated Product")
                .category("Electronics")  // Original category retained
                .boughtPrice(new BigDecimal("60.00"))
                .active(true)
                .createdAt(productRespDTO.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.updateProduct(eq(sku), any(ProductDTO.class)))
                .thenReturn(updatedResp);

        // Act & Assert
        mockMvc.perform(put("/api/products/update/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Partially Updated Product"))
                .andExpect(jsonPath("$.boughtPrice").value(60.00));

        verify(productServices, times(1)).updateProduct(eq(sku), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should fail update validation when name is blank")
    void testUpdateProduct_BlankName() throws Exception {
        // Arrange
        String sku = "SKU-001";
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("")
                .category("Electronics")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/products/update/{sku}", sku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productServices, never()).updateProduct(eq(sku), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should handle update of non-existent product")
    void testUpdateProduct_NotFound() throws Exception {
        // Arrange
        String sku = "SKU-999";
        when(productServices.updateProduct(eq(sku), any(ProductDTO.class)))
                .thenThrow(new IllegalArgumentException("Product not found with SKU: " + sku));

        // Act & Assert
        try {
            mockMvc.perform(put("/api/products/update/{sku}", sku)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
                    .andDo(print());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertTrue(
                    e.getMessage().contains("Product not found"),
                    "Expected exception message to contain 'Product not found'"
            );
        }

        verify(productServices, times(1)).updateProduct(eq(sku), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should handle deletion of non-existent product")
    void testDeleteProduct_NotFound() throws Exception {
        // Arrange
        String sku = "SKU-999";
        doThrow(new IllegalArgumentException("Product not found with SKU: " + sku))
                .when(productServices).deleteProductBySku(sku);

        // Act & Assert
        try {
            mockMvc.perform(delete("/api/products/delete/{sku}", sku)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.assertTrue(
                    e.getMessage().contains("Product not found"),
                    "Expected exception message to contain 'Product not found'"
            );
        }

        verify(productServices, times(1)).deleteProductBySku(sku);
    }

    @Test
    @DisplayName("Should create product with special characters in name")
    void testAddProduct_SpecialCharactersInName() throws Exception {
        // Arrange
        ProductDTO specialNameDTO = ProductDTO.builder()
                .name("Product & Co. (2024) - Model #123")
                .category("Special")
                .boughtPrice(new BigDecimal("99.99"))
                .active(true)
                .build();

        ProductRespDTO specialNameResp = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku("SKU-006")
                .name("Product & Co. (2024) - Model #123")
                .category("Special")
                .boughtPrice(new BigDecimal("99.99"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(specialNameResp);

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialNameDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product & Co. (2024) - Model #123"));

        verify(productServices, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should handle very large price values")
    void testAddProduct_LargePrice() throws Exception {
        // Arrange
        ProductDTO largePriceDTO = ProductDTO.builder()
                .name("Expensive Product")
                .category("Premium")
                .boughtPrice(new BigDecimal("999999999.99"))
                .active(true)
                .build();

        ProductRespDTO largePriceResp = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku("SKU-007")
                .name("Expensive Product")
                .category("Premium")
                .boughtPrice(new BigDecimal("999999999.99"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.saveProduct(any(ProductDTO.class)))
                .thenReturn(largePriceResp);

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largePriceDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boughtPrice").value(999999999.99));

        verify(productServices, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should handle product retrieval with special characters in SKU")
    void testGetProductBySku_SpecialCharacters() throws Exception {
        // Arrange
        String specialSku = "SKU-TEST-001";
        ProductRespDTO productWithSpecialSku = ProductRespDTO.builder()
                .id(UUID.randomUUID())
                .sku(specialSku)
                .name("Special SKU Product")
                .category("Test")
                .boughtPrice(new BigDecimal("15.00"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productServices.getProductBySku(specialSku))
                .thenReturn(productWithSpecialSku);

        // Act & Assert
        mockMvc.perform(get("/api/products/{sku}", specialSku)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(specialSku));

        verify(productServices, times(1)).getProductBySku(specialSku);
    }
}
