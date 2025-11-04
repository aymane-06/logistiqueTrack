package com.logitrack.logitrack.models;



import com.logitrack.logitrack.models.ENUM.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "inventory_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();

    private String referenceDocument;

    private String description;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}