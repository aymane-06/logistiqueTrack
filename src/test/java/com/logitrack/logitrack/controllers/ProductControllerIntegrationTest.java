package com.logitrack.logitrack.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.repositories.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@DisplayName("ProductController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        productRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Integration: Should create product and save to database")
    void testCreateProduct_Integration() throws Exception {
        // Arrange
        ProductDTO productDTO = ProductDTO.builder()
                .name("Integration Test Laptop")
                .category("Electronics")
                .boughtPrice(new BigDecimal("1299.99"))
                .active(true)
                .build();

        // Act
        MvcResult result = mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.boughtPrice").value(1299.99))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.sku").exists())
                .andReturn();

        // Assert - Verify in database
        String responseJson = result.getResponse().getContentAsString();
        ProductRespDTO createdProduct = objectMapper.readValue(responseJson, ProductRespDTO.class);
        
        Optional<Product> savedProduct = productRepository.findBySku(createdProduct.getSku());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getName()).isEqualTo("Integration Test Laptop");
        assertThat(savedProduct.get().getCategory()).isEqualTo("Electronics");
        assertThat(savedProduct.get().getBoughtPrice()).isEqualByComparingTo(new BigDecimal("1299.99"));
        assertThat(savedProduct.get().getActive()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Integration: Should retrieve all products from database")
    void testGetAllProducts_Integration() throws Exception {
        // Arrange - Create test data
        Product product1 = Product.builder()
                .sku("TEST-SKU-001")
                .name("Product 1")
                .category("Category A")
                .boughtPrice(new BigDecimal("100.00"))
                .active(true)
                .build();

        Product product2 = Product.builder()
                .sku("TEST-SKU-002")
                .name("Product 2")
                .category("Category B")
                .boughtPrice(new BigDecimal("200.00"))
                .active(true)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    @Order(3)
    @DisplayName("Integration: Should retrieve product by SKU from database")
    void testGetProductBySku_Integration() throws Exception {
        // Arrange
        Product product = Product.builder()
                .sku("TEST-SKU-FIND")
                .name("Findable Product")
                .category("Test Category")
                .boughtPrice(new BigDecimal("500.00"))
                .active(true)
                .build();

        productRepository.save(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/{sku}", "TEST-SKU-FIND")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST-SKU-FIND"))
                .andExpect(jsonPath("$.name").value("Findable Product"))
                .andExpect(jsonPath("$.category").value("Test Category"))
                .andExpect(jsonPath("$.boughtPrice").value(500.00));

        // Verify database state
        Optional<Product> foundProduct = productRepository.findBySku("TEST-SKU-FIND");
        assertThat(foundProduct).isPresent();
    }

    @Test
    @Order(4)
    @DisplayName("Integration: Should update product in database")
    void testUpdateProduct_Integration() throws Exception {
        // Arrange - Create initial product
        Product product = Product.builder()
                .sku("TEST-SKU-UPDATE")
                .name("Original Name")
                .category("Original Category")
                .boughtPrice(new BigDecimal("100.00"))
                .active(true)
                .build();

        productRepository.save(product);

        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Name")
                .category("Updated Category")
                .boughtPrice(new BigDecimal("150.00"))
                .active(true)
                .build();

        // Act
        mockMvc.perform(put("/api/products/update/{sku}", "TEST-SKU-UPDATE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST-SKU-UPDATE"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.category").value("Updated Category"))
                .andExpect(jsonPath("$.boughtPrice").value(150.00));

        // Assert - Verify database update
        Optional<Product> updatedProduct = productRepository.findBySku("TEST-SKU-UPDATE");
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedProduct.get().getCategory()).isEqualTo("Updated Category");
        assertThat(updatedProduct.get().getBoughtPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @Order(5)
    @DisplayName("Integration: Should delete product from database")
    void testDeleteProduct_Integration() throws Exception {
        // Arrange
        Product product = Product.builder()
                .sku("TEST-SKU-DELETE")
                .name("Product to Delete")
                .category("Temp")
                .boughtPrice(new BigDecimal("50.00"))
                .active(true)
                .build();

        productRepository.save(product);
        
        // Verify product exists before deletion
        assertThat(productRepository.findBySku("TEST-SKU-DELETE")).isPresent();

        // Act
        mockMvc.perform(delete("/api/products/delete/{sku}", "TEST-SKU-DELETE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Product with SKU: TEST-SKU-DELETE has been deleted."));

        // Assert - Verify deletion from database
        Optional<Product> deletedProduct = productRepository.findBySku("TEST-SKU-DELETE");
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("Integration: Should fail validation when name is blank")
    void testCreateProduct_ValidationError() throws Exception {
        // Arrange
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("")  // Blank name
                .category("Electronics")
                .boughtPrice(new BigDecimal("100.00"))
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify nothing was saved
        assertThat(productRepository.count()).isEqualTo(0);
    }

    @Test
    @Order(7)
    @DisplayName("Integration: Should handle product not found")
    void testGetProductBySku_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/{sku}", "NON-EXISTENT-SKU")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(8)
    @DisplayName("Integration: Should create multiple products and retrieve all")
    void testCreateAndRetrieveMultiple_Integration() throws Exception {
        // Arrange & Act - Create multiple products
        for (int i = 1; i <= 5; i++) {
            ProductDTO productDTO = ProductDTO.builder()
                    .name("Product " + i)
                    .category("Category " + (i % 2 == 0 ? "A" : "B"))
                    .boughtPrice(new BigDecimal(i * 100))
                    .active(true)
                    .build();

            mockMvc.perform(post("/api/products/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productDTO)))
                    .andExpect(status().isCreated());
        }

        // Assert - Retrieve all and verify count
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        // Verify database state
        assertThat(productRepository.count()).isEqualTo(5);
    }

    @Test
    @Order(9)
    @DisplayName("Integration: Should create product with null category")
    void testCreateProduct_NullCategory() throws Exception {
        // Arrange
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product Without Category")
                .category(null)
                .boughtPrice(new BigDecimal("75.00"))
                .active(true)
                .build();

        // Act
        MvcResult result = mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product Without Category"))
                .andReturn();

        // Assert - Verify in database
        String responseJson = result.getResponse().getContentAsString();
        ProductRespDTO createdProduct = objectMapper.readValue(responseJson, ProductRespDTO.class);
        
        Optional<Product> savedProduct = productRepository.findBySku(createdProduct.getSku());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getCategory()).isNull();
    }

    @Test
    @Order(10)
    @DisplayName("Integration: Should create inactive product")
    void testCreateProduct_Inactive() throws Exception {
        // Arrange
        ProductDTO productDTO = ProductDTO.builder()
                .name("Inactive Product")
                .category("Legacy")
                .boughtPrice(new BigDecimal("25.00"))
                .active(false)
                .build();

        // Act
        MvcResult result = mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(false))
                .andReturn();

        // Assert - Verify in database
        String responseJson = result.getResponse().getContentAsString();
        ProductRespDTO createdProduct = objectMapper.readValue(responseJson, ProductRespDTO.class);
        
        Optional<Product> savedProduct = productRepository.findBySku(createdProduct.getSku());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getActive()).isFalse();
    }

    @Test
    @Order(11)
    @DisplayName("Integration: Should update product and maintain SKU")
    void testUpdateProduct_MaintainSKU() throws Exception {
        // Arrange
        Product product = Product.builder()
                .sku("IMMUTABLE-SKU")
                .name("Original Product")
                .category("Original")
                .boughtPrice(new BigDecimal("100.00"))
                .active(true)
                .build();

        productRepository.save(product);

        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Product")
                .category("Updated")
                .boughtPrice(new BigDecimal("200.00"))
                .active(true)
                .build();

        // Act
        mockMvc.perform(put("/api/products/update/{sku}", "IMMUTABLE-SKU")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("IMMUTABLE-SKU"))
                .andExpect(jsonPath("$.name").value("Updated Product"));

        // Assert - SKU should remain unchanged
        Optional<Product> updatedProduct = productRepository.findBySku("IMMUTABLE-SKU");
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getSku()).isEqualTo("IMMUTABLE-SKU");
    }

    @Test
    @Order(12)
    @DisplayName("Integration: Should handle empty database")
    void testGetAllProducts_EmptyDatabase() throws Exception {
        // Ensure database is empty
        productRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        assertThat(productRepository.count()).isEqualTo(0);
    }

    @Test
    @Order(13)
    @DisplayName("Integration: Full CRUD workflow")
    void testFullCRUDWorkflow_Integration() throws Exception {
        // 1. Create
        ProductDTO createDTO = ProductDTO.builder()
                .name("Workflow Product")
                .category("Workflow")
                .boughtPrice(new BigDecimal("300.00"))
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String sku = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), 
                ProductRespDTO.class
        ).getSku();

        // 2. Read
        mockMvc.perform(get("/api/products/{sku}", sku))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Workflow Product"));

        // 3. Update
        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Workflow Product")
                .category("Updated Workflow")
                .boughtPrice(new BigDecimal("350.00"))
                .active(true)
                .build();

        mockMvc.perform(put("/api/products/update/{sku}", sku)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow Product"));

        // 4. Verify Update
        Optional<Product> updatedProduct = productRepository.findBySku(sku);
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getName()).isEqualTo("Updated Workflow Product");

        // 5. Delete
        mockMvc.perform(delete("/api/products/delete/{sku}", sku))
                .andExpect(status().isOk());

        // 6. Verify Deletion
        Optional<Product> deletedProduct = productRepository.findBySku(sku);
        assertThat(deletedProduct).isEmpty();
    }
}
