package com.alertacore.incidentservice.infrastructure.persistence.repository;

import com.alertacore.incidentservice.infrastructure.persistence.entity.HistorialEstadoIncidenteEntity;
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