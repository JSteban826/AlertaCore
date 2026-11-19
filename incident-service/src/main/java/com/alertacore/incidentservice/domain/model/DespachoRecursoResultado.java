package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record DespachoRecursoResultado(
        UUID incidenteId,
        UUID recursoId,
        EstadoRecurso estadoRecurso,
        EstadoIncidente estadoIncidente,
        LocalDateTime fechaDespacho
) {

    public DespachoRecursoResultado {
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
                fechaDespacho,
                "La fecha de despacho es obligatoria"
        );
    }

    public static DespachoRecursoResultado desde(
            DespachoRecurso despacho,
            EstadoIncidente estadoIncidente
    ) {
        Objects.requireNonNull(
                despacho,
                "El despacho es obligatorio"
        );

        return new DespachoRecursoResultado(
                despacho.incidenteId(),
                despacho.recursoId(),
                despacho.estado(),
                estadoIncidente,
                despacho.fechaDespacho()
        );
    }
}