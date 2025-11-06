package com.logitrack.logitrack.dtos.SalesOrder.SalesOrderLine;

import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class SalesOrderLineRespDTO {
    private String id;
    private ProductRespDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Boolean backorder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
