package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record ReservaRecurso(
        UUID reservaId,
        UUID recursoId,
        UUID incidenteId,
        EstadoRecurso estadoRecurso,
        LocalDateTime fechaReserva,
        LocalDateTime fechaVencimiento
) {

    public ReservaRecurso {
        Objects.requireNonNull(
                reservaId,
                "El identificador de la reserva es obligatorio"
        );

        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                estadoRecurso,
                "El estado del recurso es obligatorio"
        );

        Objects.requireNonNull(
                fechaReserva,
                "La fecha de reserva es obligatoria"
        );

        Objects.requireNonNull(
                fechaVencimiento,
                "La fecha de vencimiento es obligatoria"
        );
    }
}