package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaRecursoResponse(
        UUID reservaId,
        UUID recursoId,
        UUID incidenteId,
        EstadoRecurso estadoRecurso,
        LocalDateTime fechaReserva,
        LocalDateTime fechaVencimiento
) {

    public ReservaRecurso aDominio() {
        return new ReservaRecurso(
                reservaId,
                recursoId,
                incidenteId,
                estadoRecurso,
                fechaReserva,
                fechaVencimiento
        );
    }
}