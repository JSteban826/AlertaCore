package com.alertacore.incidentservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record DespacharRecursoCommand(
        UUID incidenteId,
        UUID recursoId,
        String responsable
) {

    public DespacharRecursoCommand {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                responsable,
                "El responsable es obligatorio"
        );

        if (responsable.isBlank()) {
            throw new IllegalArgumentException(
                    "El responsable no puede estar vacio"
            );
        }
    }
}