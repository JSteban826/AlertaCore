package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;

import java.util.UUID;

public record BuscarRecursoMasCercanoRequest(
        UUID incidenteId,
        TipoRecurso tipoRecurso,
        double latitudIncidente,
        double longitudIncidente,
        double distanciaMaximaKm
) {
}