package com.alertacore.resourceservice.infrastructure.persistence.repository;

import com.alertacore.resourceservice.infrastructure.persistence.entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecursoJpaRepository
        extends JpaRepository<RecursoEntity, UUID> {

    Optional<RecursoEntity> findByCodigo(
            String codigo
    );

    boolean existsByCodigo(
            String codigo
    );

    List<RecursoEntity> findByTipoAndEstado(
            String tipo,
            String estado
    );
}