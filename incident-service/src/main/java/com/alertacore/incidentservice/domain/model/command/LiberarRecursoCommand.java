package com.alertacore.incidentservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record LiberarRecursoCommand(
        UUID incidenteId,
        UUID recursoId,
        String motivo
) {

    public LiberarRecursoCommand {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                motivo,
                "El motivo es obligatorio"
        );

        if (motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo no puede estar vacio"
            );
        }
    }
}