package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record DespachoRecurso(
        UUID recursoId,
        UUID incidenteId,
        EstadoRecurso estado,
        LocalDateTime fechaDespacho
) {

    public DespachoRecurso {
        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                estado,
                "El estado del recurso es obligatorio"
        );

        Objects.requireNonNull(
                fechaDespacho,
                "La fecha de despacho es obligatoria"
        );
    }
}