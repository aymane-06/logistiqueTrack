package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.services.ProductServices;
import com.logitrack.logitrack.services.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminControllerTest")
class AdminControllerTest {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @Mock
    private ProductServices productServices;

    @InjectMocks
    private AdminController adminController;

    private UUID purchaseOrderId;
    private String productSku;
    private PurchaseOrderRespDTO purchaseOrderRespDTO;
    private Product product;

    @BeforeEach
    void setUp() {
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
    void testUpdatePurchaseOrderStatus() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", PurchaseOrderStatus.APPROVED.toString());
        
        when(purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.APPROVED))
                .thenReturn(purchaseOrderRespDTO);
        
        ResponseEntity<?> result = adminController.purchaseOrderStatus(purchaseOrderId, requestBody);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(purchaseOrderService).parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.APPROVED);
    }

    @Test
    void testUpdatePurchaseOrderStatusToPending() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", PurchaseOrderStatus.RECEIVED.toString());
        
        when(purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED))
                .thenReturn(purchaseOrderRespDTO);
        
        ResponseEntity<?> result = adminController.purchaseOrderStatus(purchaseOrderId, requestBody);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(purchaseOrderService).parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED);
    }

    @Test
    void testUpdateProductStatus() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "true");
        
        when(productServices.productStatusUpdate(productSku, true))
                .thenReturn(product);
        
        ResponseEntity<Product> result = adminController.productStatus(productSku, requestBody);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(productSku, result.getBody().getSku());
        verify(productServices).productStatusUpdate(productSku, true);
    }

    @Test
    void testUpdateProductStatusToInactive() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "false");
        
        product.setActive(false);
        
        when(productServices.productStatusUpdate(productSku, false))
                .thenReturn(product);
        
        ResponseEntity<Product> result = adminController.productStatus(productSku, requestBody);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().getActive());
        verify(productServices).productStatusUpdate(productSku, false);
    }
}
