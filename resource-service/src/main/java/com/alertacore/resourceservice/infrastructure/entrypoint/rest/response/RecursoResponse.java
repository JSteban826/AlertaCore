package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import java.util.UUID;

public record RecursoResponse(

        UUID id,

        String codigo,

        String nombre,

        TipoRecurso tipo,

        EstadoRecurso estado,

        double latitud,

        double longitud

) {
}