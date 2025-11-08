package com.logitrack.logitrack.dtos;

import com.logitrack.logitrack.models.ENUM.CarrierStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierRespDTO {
    private UUID id;
    private String code;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private BigDecimal baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private LocalTime cutOffTime;
    private CarrierStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
