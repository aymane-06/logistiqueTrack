package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final PurchaseOrderService purchaseOrderService;


    @PatchMapping("/purchaseOrder-status/update/{id}")
    public ResponseEntity<?> purchaseOrderStatus(@PathVariable UUID id, @RequestBody Map<String, String> requestBody) {
        PurchaseOrderStatus status = PurchaseOrderStatus.valueOf(requestBody.get("status"));
        PurchaseOrderRespDTO purchaseOrderRespDTO =  purchaseOrderService.parchaseOrderStatusUpdate(id, status);
        return ResponseEntity.ok().body(purchaseOrderRespDTO);
    }
}
