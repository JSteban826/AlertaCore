package com.alertacore.resourceservice.infrastructure.persistence.repository;

import com.alertacore.resourceservice.infrastructure.persistence.entity.DespachoRecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DespachoRecursoJpaRepository
        extends JpaRepository<
        DespachoRecursoEntity,
        UUID> {
}
