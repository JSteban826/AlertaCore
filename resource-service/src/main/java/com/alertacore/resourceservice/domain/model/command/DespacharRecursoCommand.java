package com.alertacore.resourceservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record DespacharRecursoCommand(

        UUID recursoId,

        UUID incidenteId,

        String responsable

) {

    public DespacharRecursoCommand {

        Objects.requireNonNull(
                recursoId,
                "El recurso es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El incidente es obligatorio"
        );

        if (responsable == null
                || responsable.isBlank()) {

            throw new IllegalArgumentException(
                    "El responsable es obligatorio"
            );
        }

        responsable = responsable.trim();
    }
}