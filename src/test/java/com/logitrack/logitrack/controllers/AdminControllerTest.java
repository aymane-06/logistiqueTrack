package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.services.ProductServices;
import com.logitrack.logitrack.services.PurchaseOrderService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminControllerTest")
class AdminControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private ProductServices productServices;

    private UUID purchaseOrderId;
    private String productSku;
    private PurchaseOrderRespDTO purchaseOrderRespDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminController(purchaseOrderService, productServices))
                .build();
        objectMapper = new ObjectMapper();

        purchaseOrderId = UUID.randomUUID();
        productSku = "SKU-12345";
        
        purchaseOrderRespDTO = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .status("CREATED")
                .build();
        
        product = new Product();
        product.setSku(productSku);
        product.setActive(true);
    }

    @Test
    @DisplayName("Should update purchase order status successfully")
    void testUpdatePurchaseOrderStatus() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", PurchaseOrderStatus.APPROVED.toString());
        
        when(purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.APPROVED))
                .thenReturn(purchaseOrderRespDTO);
        
        ResultActions response = mockMvc.perform(patch("/api/admins/purchaseOrder-status/update/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(purchaseOrderService).parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.APPROVED);
    }

    @Test
    @DisplayName("Should update purchase order status to RECEIVED")
    void testUpdatePurchaseOrderStatusToPending() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", PurchaseOrderStatus.RECEIVED.toString());
        
        when(purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED))
                .thenReturn(purchaseOrderRespDTO);
        
        ResultActions response = mockMvc.perform(patch("/api/admins/purchaseOrder-status/update/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(purchaseOrderService).parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED);
    }

    @Test
    @DisplayName("Should update product status successfully")
    void testUpdateProductStatus() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "true");
        
        when(productServices.productStatusUpdate(productSku, true))
                .thenReturn(product);
        
        ResultActions response = mockMvc.perform(patch("/api/admins/product-status/update/{sku}", productSku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(productServices).productStatusUpdate(productSku, true);
    }

    @Test
    @DisplayName("Should update product status to inactive")
    void testUpdateProductStatusToInactive() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "false");
        
        product.setActive(false);
        
        when(productServices.productStatusUpdate(productSku, false))
                .thenReturn(product);
        
        ResultActions response = mockMvc.perform(patch("/api/admins/product-status/update/{sku}", productSku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(productServices).productStatusUpdate(productSku, false);
    }
}
