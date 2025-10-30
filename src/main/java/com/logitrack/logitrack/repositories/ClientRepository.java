package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
