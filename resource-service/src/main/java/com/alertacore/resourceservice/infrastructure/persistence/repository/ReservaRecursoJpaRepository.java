package com.alertacore.resourceservice.infrastructure.persistence.repository;

import com.alertacore.resourceservice.infrastructure.persistence.entity.ReservaRecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservaRecursoJpaRepository
        extends JpaRepository<
        ReservaRecursoEntity,
        UUID> {

    Optional<ReservaRecursoEntity>
    findByClaveIdempotencia(
            String claveIdempotencia
    );

    Optional<ReservaRecursoEntity>
    findByRecursoIdAndEstado(
            UUID recursoId,
            String estado
    );
}