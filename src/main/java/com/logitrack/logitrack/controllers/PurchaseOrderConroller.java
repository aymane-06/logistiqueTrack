package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.services.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
