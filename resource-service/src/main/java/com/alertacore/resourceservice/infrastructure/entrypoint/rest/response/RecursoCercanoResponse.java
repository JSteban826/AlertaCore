package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import java.util.UUID;

public record RecursoCercanoResponse(

        UUID recursoId,

        String codigo,

        TipoRecurso tipo,

        double distanciaKm,

        int tiempoEstimadoLlegadaMinutos,

        EstadoRecurso estado

) {
}