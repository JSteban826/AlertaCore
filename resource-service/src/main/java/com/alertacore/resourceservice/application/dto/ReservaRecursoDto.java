package com.alertacore.resourceservice.application.dto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaRecursoDto(

        UUID reservaId,

        UUID recursoId,

        UUID incidenteId,

        EstadoRecurso estadoRecurso,

        LocalDateTime fechaReserva,

        LocalDateTime fechaVencimiento

) {
}