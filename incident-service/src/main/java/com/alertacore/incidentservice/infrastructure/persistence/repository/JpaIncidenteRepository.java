package com.alertacore.incidentservice.infrastructure.persistence.repository;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaIncidenteRepository
        extends JpaRepository<IncidenteEntity, UUID> {

    Page<IncidenteEntity> findByEstado(
            EstadoIncidente estado,
            Pageable pageable
    );

    Page<IncidenteEntity> findByTipo(
            TipoIncidente tipo,
            Pageable pageable
    );

    Page<IncidenteEntity> findByEstadoAndTipo(
            EstadoIncidente estado,
            TipoIncidente tipo,
            Pageable pageable
    );
}