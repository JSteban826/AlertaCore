package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record RegistroHistorialEstado(
        UUID id,
        UUID incidenteId,
        EstadoIncidente estadoAnterior,
        EstadoIncidente nuevoEstado,
        String motivo,
        String responsable,
        LocalDateTime fechaCambio
) {

    public RegistroHistorialEstado {
        Objects.requireNonNull(
                id,
                "El identificador del historial es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                nuevoEstado,
                "El nuevo estado es obligatorio"
        );

        Objects.requireNonNull(
                fechaCambio,
                "La fecha del cambio es obligatoria"
        );

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo del cambio es obligatorio"
            );
        }

        if (motivo.trim().length() > 250) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 250 caracteres"
            );
        }

        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException(
                    "El responsable del cambio es obligatorio"
            );
        }

        motivo = motivo.trim();
        responsable = responsable.trim();
    }

    public static RegistroHistorialEstado crear(
            UUID incidenteId,
            EstadoIncidente estadoAnterior,
            EstadoIncidente nuevoEstado,
            String motivo,
            String responsable
    ) {
        return new RegistroHistorialEstado(
                UUID.randomUUID(),
                incidenteId,
                estadoAnterior,
                nuevoEstado,
                motivo,
                responsable,
                LocalDateTime.now()
        );
    }
}