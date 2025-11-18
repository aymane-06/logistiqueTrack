package com.logitrack.logitrack.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.services.ProductServices;
import com.logitrack.logitrack.services.PurchaseOrderService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final PurchaseOrderService purchaseOrderService;

    private final ProductServices productServices;


    @PatchMapping("/purchaseOrder-status/update/{id}")
    public ResponseEntity<PurchaseOrderRespDTO> purchaseOrderStatus(@PathVariable UUID id, @RequestBody Map<String, String> requestBody) {
        PurchaseOrderStatus status = PurchaseOrderStatus.valueOf(requestBody.get("status"));
        PurchaseOrderRespDTO purchaseOrderRespDTO =  purchaseOrderService.parchaseOrderStatusUpdate(id, status);
        return ResponseEntity.ok().body(purchaseOrderRespDTO);
    }

    @PatchMapping("/product-status/update/{sku}")
    public ResponseEntity<Product> productStatus(@PathVariable @NotBlank String sku, @RequestBody Map<String, String> requestBody) {
        boolean status = Boolean.parseBoolean(requestBody.get("status"));
        Product responseMessage =  productServices.productStatusUpdate(sku, status);
        return ResponseEntity.ok().body(responseMessage);
    }
}
