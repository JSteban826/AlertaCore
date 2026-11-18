package com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository;

import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.HistorialEstadoIncidenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaHistorialEstadoRepository
        extends JpaRepository<
        HistorialEstadoIncidenteEntity,
        UUID
        > {

    List<HistorialEstadoIncidenteEntity>
    findByIncidenteIdOrderByFechaCambioAsc(
            UUID incidenteId
    );
}