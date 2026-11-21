package com.alertacore.notificationservice.infrastructure.persistence;

import com.alertacore.notificationservice.domain.NotificacionGateway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class NotificacionPostgresGateway implements NotificacionGateway {

    private final JdbcTemplate jdbcTemplate;

    public NotificacionPostgresGateway(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void guardarDespacho(UUID incidenteId, UUID recursoId) {
        jdbcTemplate.update(
                """
                INSERT INTO notifications.notificaciones (incidente_id, recurso_id)
                VALUES (?, ?)
                """,
                incidenteId,
                recursoId
        );
    }
}