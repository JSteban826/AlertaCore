package com.alertacore.incidentservice.domain.model.command;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;

import java.util.Objects;
import java.util.UUID;

public record AsignarRecursoCommand(
        UUID incidenteId,
        TipoRecurso tipoRecurso,
        double distanciaMaximaKm,
        int duracionReservaMinutos
) {

    public AsignarRecursoCommand {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                tipoRecurso,
                "El tipo de recurso es obligatorio"
        );

        if (distanciaMaximaKm <= 0) {
            throw new IllegalArgumentException(
                    "La distancia maxima debe ser mayor que cero"
            );
        }

        if (duracionReservaMinutos < 1) {
            throw new IllegalArgumentException(
                    "La duracion de la reserva debe ser mayor que cero"
            );
        }
    }
}