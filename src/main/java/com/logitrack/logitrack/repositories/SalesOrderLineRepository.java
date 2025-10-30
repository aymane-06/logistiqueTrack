package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
}
