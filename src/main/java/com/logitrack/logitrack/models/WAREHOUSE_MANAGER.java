package com.logitrack.logitrack.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "warehouse_managers")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WAREHOUSE_MANAGER extends User {

    @OneToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
}
