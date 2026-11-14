package com.alertacore.incidentservice.application.command;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;

import java.util.Objects;
import java.util.UUID;

public record CambiarEstadoIncidenteCommand(
        UUID incidenteId,
        EstadoIncidente nuevoEstado,
        String motivo
) {

    public CambiarEstadoIncidenteCommand {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                nuevoEstado,
                "El nuevo estado del incidente es obligatorio"
        );

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo del cambio de estado es obligatorio"
            );
        }

        if (motivo.trim().length() > 250) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 250 caracteres"
            );
        }

        motivo = motivo.trim();
    }
}