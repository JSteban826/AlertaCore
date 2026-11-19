package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record LiberacionRecurso(
        UUID recursoId,
        EstadoRecurso estado,
        LocalDateTime fechaLiberacion
) {

    public LiberacionRecurso {
        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                estado,
                "El estado del recurso es obligatorio"
        );

        Objects.requireNonNull(
                fechaLiberacion,
                "La fecha de liberacion es obligatoria"
        );
    }
}