package com.alertacore.resourceservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record ReservarRecursoCommand(

        UUID recursoId,
        UUID incidenteId,
        String claveIdempotencia,
        int duracionReservaMinutos

) {

    public ReservarRecursoCommand {

        Objects.requireNonNull(
                recursoId,
                "El recurso es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El incidente es obligatorio"
        );

        if (claveIdempotencia == null
                || claveIdempotencia.isBlank()) {

            throw new IllegalArgumentException(
                    "La clave de idempotencia es obligatoria"
            );
        }

        if (duracionReservaMinutos <= 0) {

            throw new IllegalArgumentException(
                    "La duración debe ser mayor a cero"
            );
        }
    }
}