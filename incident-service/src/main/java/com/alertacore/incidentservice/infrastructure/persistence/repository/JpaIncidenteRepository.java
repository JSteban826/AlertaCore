package com.alertacore.incidentservice.infrastructure.persistence.repository;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaIncidenteRepository
        extends JpaRepository<IncidenteEntity, UUID> {

    List<IncidenteEntity> findByEstado(
            EstadoIncidente estado
    );

    boolean existsById(UUID id);
}