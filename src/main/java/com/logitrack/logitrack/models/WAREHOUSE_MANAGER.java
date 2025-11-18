package com.logitrack.logitrack.models;

import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "warehouse_managers")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WAREHOUSE_MANAGER extends User {

    @OneToMany
    @JoinColumn(name = "warehouse_id")
    private List<Warehouse> warehouses;

    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
}
