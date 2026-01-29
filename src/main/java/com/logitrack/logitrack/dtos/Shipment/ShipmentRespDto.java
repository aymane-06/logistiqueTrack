package com.logitrack.logitrack.dtos.Shipment;

import com.logitrack.logitrack.dtos.CarrierRespDTO;

import lombok.Data;

@Data
public class ShipmentRespDto {
    private String id;
    private String salesOrderId;
    private CarrierRespDTO carrier;
    private String trackingNumber;
    private String status;
    private String plannedDate;
    private String shippedDate;
    private String deliveredDate;
    private String shippingCost;
    private Boolean isCutOffPassed;
    private String createdAt;
    private String updatedAt;
}
