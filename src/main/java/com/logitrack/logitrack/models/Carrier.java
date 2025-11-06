package com.logitrack.logitrack.models;


import com.logitrack.logitrack.models.ENUM.CarrierStatus;
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
import java.time.LocalTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "carriers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String contactEmail;
    private String contactPhone;
    @Builder.Default
    private BigDecimal baseShippingRate = BigDecimal.ZERO;
    @Builder.Default
    private Integer maxDailyCapacity = 100;
    @Builder.Default
    private Integer currentDailyShipments = 0;
    @Builder.Default
    private LocalTime cutOffTime = LocalTime.of(15, 0);

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CarrierStatus status = CarrierStatus.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void generateCode() {
        if (this.code == null || this.code.isEmpty()) {
            this.code = "CARR-" + System.currentTimeMillis();
        }
    }
}
