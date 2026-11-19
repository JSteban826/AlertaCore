package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.model.DespachoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record DespachoRecursoResponse(
        UUID recursoId,
        UUID incidenteId,
        EstadoRecurso estado,
        LocalDateTime fechaDespacho
) {

    public DespachoRecurso aDominio() {
        return new DespachoRecurso(
                recursoId,
                incidenteId,
                estado,
                fechaDespacho
        );
    }
}