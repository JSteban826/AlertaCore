package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;

import java.util.Objects;
import java.util.UUID;

public record RecursoDisponible(
        UUID recursoId,
        String codigo,
        TipoRecurso tipo,
        double distanciaKm,
        int tiempoEstimadoLlegadaMinutos,
        EstadoRecurso estado
) {

    public RecursoDisponible {
        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                codigo,
                "El codigo del recurso es obligatorio"
        );

        Objects.requireNonNull(
                tipo,
                "El tipo del recurso es obligatorio"
        );

        Objects.requireNonNull(
                estado,
                "El estado del recurso es obligatorio"
        );

        if (distanciaKm < 0) {
            throw new IllegalArgumentException(
                    "La distancia no puede ser negativa"
            );
        }

        if (tiempoEstimadoLlegadaMinutos < 0) {
            throw new IllegalArgumentException(
                    "El tiempo estimado no puede ser negativo"
            );
        }
    }
}