package com.alertacore.resourceservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record LiberarRecursoCommand(

        UUID recursoId,

        UUID incidenteId,

        String motivo

) {

    public LiberarRecursoCommand {

        Objects.requireNonNull(
                recursoId,
                "El recurso es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El incidente es obligatorio"
        );

        if (motivo == null
                || motivo.isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo es obligatorio"
            );
        }

        motivo = motivo.trim();
    }
}