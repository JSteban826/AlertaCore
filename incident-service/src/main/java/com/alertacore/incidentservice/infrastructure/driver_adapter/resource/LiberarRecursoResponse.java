package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record LiberarRecursoResponse(
        UUID recursoId,
        EstadoRecurso estado,
        LocalDateTime fechaLiberacion
) {
}