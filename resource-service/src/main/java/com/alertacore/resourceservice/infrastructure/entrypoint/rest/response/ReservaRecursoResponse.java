package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

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
}
