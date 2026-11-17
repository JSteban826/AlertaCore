package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(

        LocalDateTime timestamp,

        int estado,

        String codigo,

        String mensaje,

        String ruta,

        Map<String, String> erroresValidacion

) {
}