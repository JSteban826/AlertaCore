package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record LiberacionRecursoResultado(
        UUID incidenteId,
        UUID recursoId,
        EstadoRecurso estadoRecurso,
        EstadoIncidente estadoIncidente,
        LocalDateTime fechaLiberacion
) {

    public LiberacionRecursoResultado {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                estadoRecurso,
                "El estado del recurso es obligatorio"
        );

        Objects.requireNonNull(
                estadoIncidente,
                "El estado del incidente es obligatorio"
        );

        Objects.requireNonNull(
                fechaLiberacion,
                "La fecha de liberacion es obligatoria"
        );
    }
}