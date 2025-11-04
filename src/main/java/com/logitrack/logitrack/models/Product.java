package com.logitrack.logitrack.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    private Boolean active = true;

    private BigDecimal boughtPrice;

    @OneToMany
    @JoinColumn(name = "inventory_id")
    private List<Inventory> inventory;

    @OneToMany
    @JoinColumn(name = "purchase_order_line_id")
    private List<PurchaseOrderLine> purchaseOrderLine;

    @OneToMany
    @JoinColumn(name = "sales_order_line_id")
    private List<SalesOrderLine> salesOrderLines;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateSkuIfAbsent() {
        if (this.sku == null || this.sku.isEmpty()) {
            this.sku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}