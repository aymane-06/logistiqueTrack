package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.services.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderConroller {
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/create")
    public ResponseEntity<PurchaseOrderRespDTO> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrderRespDTO purchaseOrderRespDTO = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderRespDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PurchaseOrderRespDTO>> getAllPurchaseOrders() {
        List<PurchaseOrderRespDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(purchaseOrders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderRespDTO> getPurchaseOrderById(@PathVariable("id") UUID id) {
        PurchaseOrderRespDTO purchaseOrderRespDTO = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(purchaseOrderRespDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PurchaseOrderRespDTO> updatePurchaseOrder(@PathVariable("id") UUID id, @Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrderRespDTO updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id, purchaseOrderDTO);
        return ResponseEntity.ok(updatedPurchaseOrder);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePurchaseOrderById(@PathVariable("id") UUID id) {
        PurchaseOrderRespDTO deletedPurchaseOrder = purchaseOrderService.deletePurchaseOrderById(id);
        return ResponseEntity.ok().body("Deleted Purchase Order: " + deletedPurchaseOrder);
    }
}
