package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;

import java.util.UUID;

public record RecursoCercanoResponse(
        UUID recursoId,
        String codigo,
        TipoRecurso tipo,
        double distanciaKm,
        int tiempoEstimadoLlegadaMinutos,
        EstadoRecurso estado
) {

    public RecursoDisponible aDominio() {
        return new RecursoDisponible(
                recursoId,
                codigo,
                tipo,
                distanciaKm,
                tiempoEstimadoLlegadaMinutos,
                estado
        );
    }
}