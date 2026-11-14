package com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception;

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

    public static ErrorResponse crear(
            int estado,
            String codigo,
            String mensaje,
            String ruta
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                estado,
                codigo,
                mensaje,
                ruta,
                Map.of()
        );
    }

    public static ErrorResponse crearConValidaciones(
            int estado,
            String codigo,
            String mensaje,
            String ruta,
            Map<String, String> erroresValidacion
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                estado,
                codigo,
                mensaje,
                ruta,
                Map.copyOf(erroresValidacion)
        );
    }
}