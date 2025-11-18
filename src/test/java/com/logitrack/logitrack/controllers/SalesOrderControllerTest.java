package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;
import com.logitrack.logitrack.services.SalesOrderService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesOrderControllerTest")
class SalesOrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SalesOrderService salesOrderService;

    private SalesOrderDTO salesOrderDTO;
    private SalesOrderRespDTO salesOrderRespDTO;
    private UUID orderId;
    private UUID carrierId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SalesOrederController(salesOrderService))
                .build();
        objectMapper = new ObjectMapper();

        orderId = UUID.randomUUID();
        carrierId = UUID.randomUUID();
        
        salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setClientId(UUID.randomUUID());
        salesOrderDTO.setWarehouseId(UUID.randomUUID());
        salesOrderDTO.setLines(List.of());

        salesOrderRespDTO = new SalesOrderRespDTO();
        salesOrderRespDTO.setId(orderId.toString());
    }

    @Test
    @DisplayName("Should create sales order successfully")
    void testCreateSalesOrder() throws Exception {
        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class))).thenReturn(salesOrderRespDTO);

        ResultActions response = mockMvc.perform(post("/api/sales-orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salesOrderDTO)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));
    }

    @Test
    @DisplayName("Should retrieve all sales orders")
    void testGetAllSalesOrders() throws Exception {
        SalesOrderRespDTO order2 = new SalesOrderRespDTO();
        order2.setId(UUID.randomUUID().toString());

        when(salesOrderService.getAllSalesOrders()).thenReturn(List.of(salesOrderRespDTO, order2));

        ResultActions response = mockMvc.perform(get("/api/sales-orders/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).getAllSalesOrders();
    }

    @Test
    @DisplayName("Should get sales order by ID successfully")
    void testGetSalesOrderById() throws Exception {
        when(salesOrderService.getSalesOrderById(orderId)).thenReturn(salesOrderRespDTO);

        ResultActions response = mockMvc.perform(get("/api/sales-orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).getSalesOrderById(orderId);
    }

    @Test
    @DisplayName("Should reserve sales order successfully")
    void testReserveSalesOrder() throws Exception {
        when(salesOrderService.reserveSalesOrder(orderId)).thenReturn(salesOrderRespDTO);

        ResultActions response = mockMvc.perform(put("/api/sales-orders/{id}/reserve", orderId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).reserveSalesOrder(orderId);
    }

    @Test
    @DisplayName("Should ship sales order successfully")
    void testShipSalesOrder() throws Exception {
        when(salesOrderService.shipSalesOrder(orderId, carrierId)).thenReturn(salesOrderRespDTO);

        Map<String, String> shipmentDetails = Map.of("carrierId", carrierId.toString());

        ResultActions response = mockMvc.perform(put("/api/sales-orders/{id}/ship", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shipmentDetails)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).shipSalesOrder(orderId, carrierId);
    }

    @Test
    @DisplayName("Should deliver sales order successfully")
    void testDeliverSalesOrder() throws Exception {
        when(salesOrderService.deliverSalesOrder(orderId)).thenReturn(salesOrderRespDTO);

        ResultActions response = mockMvc.perform(put("/api/sales-orders/{id}/deliver", orderId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(salesOrderService).deliverSalesOrder(orderId);
    }

    @Test
    @DisplayName("Should handle invalid sales order data on create")
    void testCreateSalesOrderInvalid() throws Exception {
        SalesOrderDTO invalidDTO = new SalesOrderDTO();

        mockMvc.perform(post("/api/sales-orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(salesOrderService, never()).createSalesOrder(any());
    }
}