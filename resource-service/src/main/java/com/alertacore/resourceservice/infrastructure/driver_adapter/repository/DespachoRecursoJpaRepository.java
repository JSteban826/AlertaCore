package com.alertacore.resourceservice.infrastructure.driver_adapter.repository;

import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.DespachoRecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DespachoRecursoJpaRepository
        extends JpaRepository<
        DespachoRecursoEntity,
        UUID> {
}
