package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
}
