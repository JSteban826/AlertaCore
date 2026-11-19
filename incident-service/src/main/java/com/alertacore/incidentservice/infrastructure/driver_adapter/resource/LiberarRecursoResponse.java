package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.model.LiberacionRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record LiberarRecursoResponse(
        UUID recursoId,
        EstadoRecurso estado,
        LocalDateTime fechaLiberacion
) {

    public LiberacionRecurso aDominio() {
        return new LiberacionRecurso(
                recursoId,
                estado,
                fechaLiberacion
        );
    }
}
