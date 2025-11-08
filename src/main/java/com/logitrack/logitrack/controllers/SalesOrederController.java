package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderDTO;
import com.logitrack.logitrack.dtos.SalesOrder.SalesOrderRespDTO;
import com.logitrack.logitrack.services.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales-orders")
public class SalesOrederController {

    private final SalesOrderService salesOrderService;

    @PostMapping("/create")
    public ResponseEntity<?> createSalesOrder(@Valid @RequestBody SalesOrderDTO salesOrderDTO) {
        SalesOrderRespDTO salesOrderRespDTO = salesOrderService.createSalesOrder(salesOrderDTO);
        return  ResponseEntity.ok(salesOrderRespDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSalesOrders() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
    }

    @PutMapping("/{id}/reserve")
    public ResponseEntity<?> reserveSalesOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(salesOrderService.reserveSalesOrder(id));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipSalesOrder(@PathVariable UUID id,@RequestBody Map<String, String> shipmentDetails) {
        UUID carierId = UUID.fromString(shipmentDetails.get("carrierId"));
        return ResponseEntity.ok(salesOrderService.shipSalesOrder(id,carierId));
        }

        @PutMapping("/{id}/deliver")
        public ResponseEntity<?> deliverSalesOrder(@PathVariable UUID id) {
            return ResponseEntity.ok(salesOrderService.deliverSalesOrder(id));
        }
}
