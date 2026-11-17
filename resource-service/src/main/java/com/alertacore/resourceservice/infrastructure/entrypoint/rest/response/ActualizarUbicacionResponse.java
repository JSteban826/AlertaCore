package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActualizarUbicacionResponse(

        UUID recursoId,

        double latitud,

        double longitud,

        LocalDateTime fechaActualizacion

) {
}