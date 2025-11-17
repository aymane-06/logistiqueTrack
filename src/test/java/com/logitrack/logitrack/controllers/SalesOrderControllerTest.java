package com.logitrack.logitrack.controllers;package com.logitrack.logitrack.controllers;package com.logitrack.logitrack.controllers;package com.logitrack.logitrack.controllers;



import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;

import com.logitrack.logitrack.services.SalesOrderService;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;import com.logitrack.logitrack.services.SalesOrderService;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;

import org.mockito.InjectMocks;

import org.mockito.Mock;import org.junit.jupiter.api.BeforeEach;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;import org.junit.jupiter.api.DisplayName;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;

import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.util.HashMap;import org.junit.jupiter.api.extension.ExtendWith;import com.logitrack.logitrack.services.SalesOrderService;import com.logitrack.logitrack.services.SalesOrderService;

import java.util.List;

import java.util.Map;import org.mockito.InjectMocks;

import java.util.UUID;

import org.mockito.Mock;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;import org.junit.jupiter.api.DisplayName;import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)

@DisplayName("SalesOrderControllerTest")import org.springframework.http.ResponseEntity;

class SalesOrderControllerTest {

import org.junit.jupiter.api.Test;import org.junit.jupiter.api.Test;

    @Mock

    private SalesOrderService salesOrderService;import java.util.ArrayList;



    @InjectMocksimport java.util.HashMap;import org.junit.jupiter.api.extension.ExtendWith;import org.junit.jupiter.api.extension.ExtendWith;

    private SalesOrederController salesOrderController;

import java.util.List;

    private SalesOrderDTO salesOrderDTO;

    private SalesOrderRespDTO salesOrderRespDTO;import java.util.Map;import org.mockito.InjectMocks;import org.mockito.InjectMocks;

    private UUID orderId;

    private UUID carrierId;import java.util.UUID;



    @BeforeEachimport org.mockito.Mock;import org.mockito.Mock;

    void setUp() {

        orderId = UUID.randomUUID();import static org.junit.jupiter.api.Assertions.*;

        carrierId = UUID.randomUUID();

        import static org.mockito.ArgumentMatchers.any;import org.mockito.junit.jupiter.MockitoExtension;import org.mockito.junit.jupiter.MockitoExtension;

        salesOrderDTO = new SalesOrderDTO();

        salesOrderDTO.setClientId(UUID.randomUUID());import static org.mockito.Mockito.*;

        salesOrderDTO.setWarehouseId(UUID.randomUUID());

        salesOrderDTO.setLines(new ArrayList<>());import org.springframework.http.HttpStatus;import org.springframework.http.HttpStatus;

        

        salesOrderRespDTO = new SalesOrderRespDTO();@ExtendWith(MockitoExtension.class)

        salesOrderRespDTO.setId(orderId.toString());

    }@DisplayName("SalesOrderControllerTest")import org.springframework.http.ResponseEntity;import org.springframework.http.ResponseEntity;



    @Testclass SalesOrderControllerTest {

    void testCreateSalesOrder() {

        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class)))

                .thenReturn(salesOrderRespDTO);

            @Mock

        ResponseEntity<?> result = salesOrderController.createSalesOrder(salesOrderDTO);

            private SalesOrderService salesOrderService;import java.util.ArrayList;import java.util.ArrayList;

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());

        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));

    }    @InjectMocksimport java.util.HashMap;import java.util.HashMap;



    @Test    private SalesOrederController salesOrderController;

    void testGetAllSalesOrders() {

        List<SalesOrderRespDTO> orders = new ArrayList<>();import java.util.List;import java.util.List;

        orders.add(salesOrderRespDTO);

        when(salesOrderService.getAllSalesOrders()).thenReturn(orders);    private SalesOrderDTO salesOrderDTO;

        

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();    private SalesOrderRespDTO salesOrderRespDTO;import java.util.Map;import java.util.Map;

        

        assertEquals(HttpStatus.OK, result.getStatusCode());    private UUID orderId;

        assertNotNull(result.getBody());

        verify(salesOrderService).getAllSalesOrders();    private UUID carrierId;import java.util.UUID;import java.util.UUID;

    }



    @Test

    void testGetAllSalesOrdersEmpty() {    @BeforeEach

        when(salesOrderService.getAllSalesOrders()).thenReturn(new ArrayList<>());

            void setUp() {

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();

                orderId = UUID.randomUUID();import static org.junit.jupiter.api.Assertions.*;import static org.junit.jupiter.api.Assertions.*;

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        carrierId = UUID.randomUUID();

        verify(salesOrderService).getAllSalesOrders();

    }        import static org.mockito.ArgumentMatchers.any;import static org.mockito.ArgumentMatchers.any;



    @Test        salesOrderDTO = new SalesOrderDTO();

    void testGetSalesOrderById() {

        when(salesOrderService.getSalesOrderById(orderId))        salesOrderDTO.setClientId(UUID.randomUUID());import static org.mockito.Mockito.*;import static org.mockito.Mockito.*;

                .thenReturn(salesOrderRespDTO);

                salesOrderDTO.setWarehouseId(UUID.randomUUID());

        ResponseEntity<?> result = salesOrderController.getSalesOrderById(orderId);

                salesOrderDTO.setLines(new ArrayList<>());

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        

        verify(salesOrderService).getSalesOrderById(orderId);

    }        salesOrderRespDTO = new SalesOrderRespDTO();@ExtendWith(MockitoExtension.class)@ExtendWith(MockitoExtension.class)



    @Test        salesOrderRespDTO.setId(orderId.toString());

    void testReserveSalesOrder() {

        when(salesOrderService.reserveSalesOrder(orderId))    }@DisplayName("SalesOrderControllerTest")@DisplayName("SalesOrderControllerTest")

                .thenReturn(salesOrderRespDTO);

        

        ResponseEntity<?> result = salesOrderController.reserveSalesOrder(orderId);

            @Testclass SalesOrderControllerTest {class SalesOrderControllerTest {

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());    void testCreateSalesOrder() {

        verify(salesOrderService).reserveSalesOrder(orderId);

    }        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class)))



    @Test                .thenReturn(salesOrderRespDTO);

    void testShipSalesOrder() {

        Map<String, String> shipmentDetails = new HashMap<>();            @Mock    @Mock

        shipmentDetails.put("carrierId", carrierId.toString());

                ResponseEntity<?> result = salesOrderController.createSalesOrder(salesOrderDTO);

        when(salesOrderService.shipSalesOrder(orderId, carrierId))

                .thenReturn(salesOrderRespDTO);            private SalesOrderService salesOrderService;    private SalesOrderService salesOrderService;

        

        ResponseEntity<?> result = salesOrderController.shipSalesOrder(orderId, shipmentDetails);        assertEquals(HttpStatus.OK, result.getStatusCode());

        

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertNotNull(result.getBody());

        assertNotNull(result.getBody());

        verify(salesOrderService).shipSalesOrder(orderId, carrierId);        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));

    }

    }    @InjectMocks    @InjectMocks

    @Test

    void testDeliverSalesOrder() {

        when(salesOrderService.deliverSalesOrder(orderId))

                .thenReturn(salesOrderRespDTO);    @Test    private SalesOrederController salesOrderController;    private SalesOrederController salesOrderController;

        

        ResponseEntity<?> result = salesOrderController.deliverSalesOrder(orderId);    void testGetAllSalesOrders() {

        

        assertEquals(HttpStatus.OK, result.getStatusCode());        List<SalesOrderRespDTO> orders = new ArrayList<>();

        assertNotNull(result.getBody());

        verify(salesOrderService).deliverSalesOrder(orderId);        orders.add(salesOrderRespDTO);

    }

}        when(salesOrderService.getAllSalesOrders()).thenReturn(orders);    private SalesOrderDTO salesOrderDTO;    private SalesOrderDTO salesOrderDTO;


        

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();    private SalesOrderRespDTO salesOrderRespDTO;    private SalesOrderRespDTO salesOrderRespDTO;

        

        assertEquals(HttpStatus.OK, result.getStatusCode());    private UUID orderId;    private UUID orderId;

        assertNotNull(result.getBody());

        verify(salesOrderService).getAllSalesOrders();    private UUID carrierId;    private UUID carrierId;

    }



    @Test

    void testGetAllSalesOrdersEmpty() {    @BeforeEach    @BeforeEach

        when(salesOrderService.getAllSalesOrders()).thenReturn(new ArrayList<>());

            void setUp() {    void setUp() {

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();

                orderId = UUID.randomUUID();        orderId = UUID.randomUUID();

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        carrierId = UUID.randomUUID();        carrierId = UUID.randomUUID();

        verify(salesOrderService).getAllSalesOrders();

    }                



    @Test        salesOrderDTO = new SalesOrderDTO();        salesOrderDTO = new SalesOrderDTO();

    void testGetSalesOrderById() {

        when(salesOrderService.getSalesOrderById(orderId))        salesOrderDTO.setClientId(UUID.randomUUID());        salesOrderDTO.setClientId(UUID.randomUUID());

                .thenReturn(salesOrderRespDTO);

                salesOrderDTO.setWarehouseId(UUID.randomUUID());        salesOrderDTO.setWarehouseId(UUID.randomUUID());

        ResponseEntity<?> result = salesOrderController.getSalesOrderById(orderId);

                salesOrderDTO.setLines(new ArrayList<>());        salesOrderDTO.setLines(new ArrayList<>());

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());                

        verify(salesOrderService).getSalesOrderById(orderId);

    }        salesOrderRespDTO = new SalesOrderRespDTO();        salesOrderRespDTO = new SalesOrderRespDTO();



    @Test        salesOrderRespDTO.setId(orderId.toString());        salesOrderRespDTO.setId(orderId.toString());

    void testReserveSalesOrder() {

        when(salesOrderService.reserveSalesOrder(orderId))    }    }

                .thenReturn(salesOrderRespDTO);

        

        ResponseEntity<?> result = salesOrderController.reserveSalesOrder(orderId);

            @Test    @Test

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());    void testCreateSalesOrder() {    void testCreateSalesOrder() {

        verify(salesOrderService).reserveSalesOrder(orderId);

    }        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class)))        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class)))



    @Test                .thenReturn(salesOrderRespDTO);                .thenReturn(salesOrderRespDTO);

    void testShipSalesOrder() {

        Map<String, String> shipmentDetails = new HashMap<>();                

        shipmentDetails.put("carrierId", carrierId.toString());

                ResponseEntity<?> result = salesOrderController.createSalesOrder(salesOrderDTO);        ResponseEntity<?> result = salesOrderController.createSalesOrder(salesOrderDTO);

        when(salesOrderService.shipSalesOrder(orderId, carrierId))

                .thenReturn(salesOrderRespDTO);                

        

        ResponseEntity<?> result = salesOrderController.shipSalesOrder(orderId, shipmentDetails);        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        assertNotNull(result.getBody());

        verify(salesOrderService).shipSalesOrder(orderId, carrierId);        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));

    }

    }    }

    @Test

    void testDeliverSalesOrder() {

        when(salesOrderService.deliverSalesOrder(orderId))

                .thenReturn(salesOrderRespDTO);    @Test    @Test

        

        ResponseEntity<?> result = salesOrderController.deliverSalesOrder(orderId);    void testGetAllSalesOrders() {    void testGetAllSalesOrders() {

        

        assertEquals(HttpStatus.OK, result.getStatusCode());        List<SalesOrderRespDTO> orders = new ArrayList<>();        List<SalesOrderRespDTO> orders = new ArrayList<>();

        assertNotNull(result.getBody());

        verify(salesOrderService).deliverSalesOrder(orderId);        orders.add(salesOrderRespDTO);        orders.add(salesOrderRespDTO);

    }

}        when(salesOrderService.getAllSalesOrders()).thenReturn(orders);        when(salesOrderService.getAllSalesOrders()).thenReturn(orders);


                

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).getAllSalesOrders();        verify(salesOrderService).getAllSalesOrders();

    }    }



    @Test    @Test

    void testGetAllSalesOrdersEmpty() {    void testGetAllSalesOrdersEmpty() {

        when(salesOrderService.getAllSalesOrders()).thenReturn(new ArrayList<>());        when(salesOrderService.getAllSalesOrders()).thenReturn(new ArrayList<>());

                

        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).getAllSalesOrders();        verify(salesOrderService).getAllSalesOrders();

    }    }



    @Test    @Test

    void testGetSalesOrderById() {    void testGetSalesOrderById() {

        when(salesOrderService.getSalesOrderById(orderId))        when(salesOrderService.getSalesOrderById(orderId))

                .thenReturn(salesOrderRespDTO);                .thenReturn(salesOrderRespDTO);

                

        ResponseEntity<?> result = salesOrderController.getSalesOrderById(orderId);        ResponseEntity<?> result = salesOrderController.getSalesOrderById(orderId);

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).getSalesOrderById(orderId);        verify(salesOrderService).getSalesOrderById(orderId);

    }    }



    @Test    @Test

    void testReserveSalesOrder() {    void testReserveSalesOrder() {

        when(salesOrderService.reserveSalesOrder(orderId))        when(salesOrderService.reserveSalesOrder(orderId))

                .thenReturn(salesOrderRespDTO);                .thenReturn(salesOrderRespDTO);

                

        ResponseEntity<?> result = salesOrderController.reserveSalesOrder(orderId);        ResponseEntity<?> result = salesOrderController.reserveSalesOrder(orderId);

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).reserveSalesOrder(orderId);        verify(salesOrderService).reserveSalesOrder(orderId);

    }    }



    @Test    @Test

    void testShipSalesOrder() {    void testShipSalesOrder() {

        Map<String, String> shipmentDetails = new HashMap<>();        Map<String, String> shipmentDetails = new HashMap<>();

        shipmentDetails.put("carrierId", carrierId.toString());        shipmentDetails.put("carrierId", carrierId.toString());

                

        when(salesOrderService.shipSalesOrder(orderId, carrierId))        when(salesOrderService.shipSalesOrder(orderId, carrierId))

                .thenReturn(salesOrderRespDTO);                .thenReturn(salesOrderRespDTO);

                

        ResponseEntity<?> result = salesOrderController.shipSalesOrder(orderId, shipmentDetails);        ResponseEntity<?> result = salesOrderController.shipSalesOrder(orderId, shipmentDetails);

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).shipSalesOrder(orderId, carrierId);        verify(salesOrderService).shipSalesOrder(orderId, carrierId);

    }    }



    @Test    @Test

    void testDeliverSalesOrder() {    void testDeliverSalesOrder() {

        when(salesOrderService.deliverSalesOrder(orderId))        when(salesOrderService.deliverSalesOrder(orderId))

                .thenReturn(salesOrderRespDTO);                .thenReturn(salesOrderRespDTO);

                

        ResponseEntity<?> result = salesOrderController.deliverSalesOrder(orderId);        ResponseEntity<?> result = salesOrderController.deliverSalesOrder(orderId);

                

        assertEquals(HttpStatus.OK, result.getStatusCode());        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertNotNull(result.getBody());        assertNotNull(result.getBody());

        verify(salesOrderService).deliverSalesOrder(orderId);        verify(salesOrderService).deliverSalesOrder(orderId);

    }    }

}}

    }

    @Test
    void testCreateSalesOrder() {
        when(salesOrderService.createSalesOrder(any(SalesOrderDTO.class)))
                .thenReturn(salesOrderRespDTO);
        
        ResponseEntity<?> result = salesOrderController.createSalesOrder(salesOrderDTO);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).createSalesOrder(any(SalesOrderDTO.class));
    }

    @Test
    void testGetAllSalesOrders() {
        List<SalesOrderRespDTO> orders = new ArrayList<>();
        orders.add(salesOrderRespDTO);
        when(salesOrderService.getAllSalesOrders()).thenReturn(orders);
        
        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).getAllSalesOrders();
    }

    @Test
    void testGetAllSalesOrdersEmpty() {
        when(salesOrderService.getAllSalesOrders()).thenReturn(new ArrayList<>());
        
        ResponseEntity<?> result = salesOrderController.getAllSalesOrders();
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).getAllSalesOrders();
    }

    @Test
    void testGetSalesOrderById() {
        when(salesOrderService.getSalesOrderById(orderId))
                .thenReturn(salesOrderRespDTO);
        
        ResponseEntity<?> result = salesOrderController.getSalesOrderById(orderId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).getSalesOrderById(orderId);
    }

    @Test
    void testReserveSalesOrder() {
        SalesOrderRespDTO reservedOrder = SalesOrderRespDTO.builder()
                .id(orderId)
                .customerId("CUST-001")
                .totalAmount(500.0)
                .status("RESERVED")
                .build();
        
        when(salesOrderService.reserveSalesOrder(orderId))
                .thenReturn(reservedOrder);
        
        ResponseEntity<?> result = salesOrderController.reserveSalesOrder(orderId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).reserveSalesOrder(orderId);
    }

    @Test
    void testShipSalesOrder() {
        SalesOrderRespDTO shippedOrder = SalesOrderRespDTO.builder()
                .id(orderId)
                .customerId("CUST-001")
                .totalAmount(500.0)
                .status("SHIPPED")
                .build();
        
        Map<String, String> shipmentDetails = new HashMap<>();
        shipmentDetails.put("carrierId", carrierId.toString());
        
        when(salesOrderService.shipSalesOrder(orderId, carrierId))
                .thenReturn(shippedOrder);
        
        ResponseEntity<?> result = salesOrderController.shipSalesOrder(orderId, shipmentDetails);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).shipSalesOrder(orderId, carrierId);
    }

    @Test
    void testDeliverSalesOrder() {
        SalesOrderRespDTO deliveredOrder = SalesOrderRespDTO.builder()
                .id(orderId)
                .customerId("CUST-001")
                .totalAmount(500.0)
                .status("DELIVERED")
                .build();
        
        when(salesOrderService.deliverSalesOrder(orderId))
                .thenReturn(deliveredOrder);
        
        ResponseEntity<?> result = salesOrderController.deliverSalesOrder(orderId);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(salesOrderService).deliverSalesOrder(orderId);
    }
}
