package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record DespachoRecursoResponse(

        UUID recursoId,

        UUID incidenteId,

        EstadoRecurso estado,

        LocalDateTime fechaDespacho

) {
}