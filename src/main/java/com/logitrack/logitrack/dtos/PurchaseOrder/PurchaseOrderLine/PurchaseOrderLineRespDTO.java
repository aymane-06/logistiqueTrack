package com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine;

import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderLineRespDTO {
    private ProductRespDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
}
