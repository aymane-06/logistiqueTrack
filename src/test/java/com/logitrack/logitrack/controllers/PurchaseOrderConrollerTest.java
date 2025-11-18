package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.OrderWarehouseRespDTO;
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
@DisplayName("PurchaseOrderControllerTest")
class PurchaseOrderConrollerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrderDTO purchaseOrderDTO;
    private PurchaseOrderRespDTO purchaseOrderRespDTO;
    private UUID purchaseOrderId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PurchaseOrderConroller(purchaseOrderService))
                .build();
        objectMapper = new ObjectMapper();

        purchaseOrderId = UUID.randomUUID();
        purchaseOrderDTO = PurchaseOrderDTO.builder()
                .warehouseId(UUID.randomUUID())
                .supplierId(UUID.randomUUID())
                .lines(List.of())
                .build();

        purchaseOrderRespDTO = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .warehouse(OrderWarehouseRespDTO.builder().id(UUID.randomUUID()).build())
                .status("CREATED")
                .expectedDelivery(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create purchase order successfully")
    void testCreatePurchaseOrder() throws Exception {
        when(purchaseOrderService.createPurchaseOrder(any(PurchaseOrderDTO.class)))
                .thenReturn(purchaseOrderRespDTO);

        ResultActions response = mockMvc.perform(post("/api/purchase-orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseOrderDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(purchaseOrderId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(purchaseOrderService).createPurchaseOrder(any(PurchaseOrderDTO.class));
    }

    @Test
    @DisplayName("Should retrieve all purchase orders")
    void testGetAllPurchaseOrders() throws Exception {
        PurchaseOrderRespDTO po2 = PurchaseOrderRespDTO.builder()
                .id(UUID.randomUUID())
                .status("RECEIVED")
                .build();

        when(purchaseOrderService.getAllPurchaseOrders())
                .thenReturn(List.of(purchaseOrderRespDTO, po2));

        ResultActions response = mockMvc.perform(get("/api/purchase-orders/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(purchaseOrderService).getAllPurchaseOrders();
    }

    @Test
    @DisplayName("Should retrieve purchase order by ID")
    void testGetPurchaseOrderById() throws Exception {
        when(purchaseOrderService.getPurchaseOrderById(purchaseOrderId))
                .thenReturn(purchaseOrderRespDTO);

        ResultActions response = mockMvc.perform(get("/api/purchase-orders/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchaseOrderId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(purchaseOrderService).getPurchaseOrderById(purchaseOrderId);
    }

    @Test
    @DisplayName("Should update purchase order successfully")
    void testUpdatePurchaseOrder() throws Exception {
        PurchaseOrderRespDTO updatedPO = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .status("APPROVED")
                .expectedDelivery(LocalDateTime.now().plusDays(5))
                .build();

        when(purchaseOrderService.updatePurchaseOrder(eq(purchaseOrderId), any(PurchaseOrderDTO.class)))
                .thenReturn(updatedPO);

        ResultActions response = mockMvc.perform(put("/api/purchase-orders/update/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseOrderDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchaseOrderId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(purchaseOrderService).updatePurchaseOrder(eq(purchaseOrderId), any(PurchaseOrderDTO.class));
    }

    @Test
    @DisplayName("Should delete purchase order successfully")
    void testDeletePurchaseOrder() throws Exception {
        when(purchaseOrderService.deletePurchaseOrderById(purchaseOrderId))
                .thenReturn(purchaseOrderRespDTO);

        ResultActions response = mockMvc.perform(delete("/api/purchase-orders/delete/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deleted Purchase Order")));

        verify(purchaseOrderService).deletePurchaseOrderById(purchaseOrderId);
    }

    @Test
    @DisplayName("Should handle empty purchase orders list")
    void testGetAllPurchaseOrdersEmpty() throws Exception {
        when(purchaseOrderService.getAllPurchaseOrders()).thenReturn(List.of());

        ResultActions response = mockMvc.perform(get("/api/purchase-orders/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(purchaseOrderService).getAllPurchaseOrders();
    }

    @Test
    @DisplayName("Should handle multiple purchase orders retrieval")
    void testGetAllPurchaseOrdersMultiple() throws Exception {
        List<PurchaseOrderRespDTO> poList = List.of(
                purchaseOrderRespDTO,
                PurchaseOrderRespDTO.builder()
                        .id(UUID.randomUUID())
                        .status("APPROVED")
                        .build(),
                PurchaseOrderRespDTO.builder()
                        .id(UUID.randomUUID())
                        .status("RECEIVED")
                        .build()
        );

        when(purchaseOrderService.getAllPurchaseOrders()).thenReturn(poList);

        ResultActions response = mockMvc.perform(get("/api/purchase-orders/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(purchaseOrderService).getAllPurchaseOrders();
    }

    @Test
    @DisplayName("Should create purchase order with PENDING status")
    void testCreatePurchaseOrderWithPendingStatus() throws Exception {
        PurchaseOrderDTO pendingPO = PurchaseOrderDTO.builder()
                .warehouseId(UUID.randomUUID())
                .supplierId(UUID.randomUUID())
                .lines(List.of())
                .build();

        PurchaseOrderRespDTO pendingResp = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .status("APPROVED")
                .build();

        when(purchaseOrderService.createPurchaseOrder(any(PurchaseOrderDTO.class)))
                .thenReturn(pendingResp);

        ResultActions response = mockMvc.perform(post("/api/purchase-orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pendingPO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(purchaseOrderService).createPurchaseOrder(any(PurchaseOrderDTO.class));
    }

    @Test
    @DisplayName("Should update purchase order to DELIVERED status")
    void testUpdatePurchaseOrderToDelivered() throws Exception {
        PurchaseOrderDTO updateDTO = PurchaseOrderDTO.builder()
                .warehouseId(UUID.randomUUID())
                .supplierId(UUID.randomUUID())
                .lines(List.of())
                .build();

        PurchaseOrderRespDTO deliveredResp = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .status("RECEIVED")
                .build();

        when(purchaseOrderService.updatePurchaseOrder(eq(purchaseOrderId), any(PurchaseOrderDTO.class)))
                .thenReturn(deliveredResp);

        ResultActions response = mockMvc.perform(put("/api/purchase-orders/update/{id}", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));

        verify(purchaseOrderService).updatePurchaseOrder(eq(purchaseOrderId), any(PurchaseOrderDTO.class));
    }
}
