package com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
        name = "ErrorResponse",
        description = "Respuesta uniforme para los errores de la API"
)
public record ErrorResponse(

        @Schema(
                description = "Fecha y hora en que ocurrió el error",
                example = "2026-11-14T23:09:09.1369341"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "Código de estado HTTP",
                example = "404"
        )
        int estado,

        @Schema(
                description = "Código interno identificador del error",
                example = "INCIDENTE_NO_ENCONTRADO"
        )
        String codigo,

        @Schema(
                description = "Descripción legible del error",
                example = "No se encontró el incidente solicitado"
        )
        String mensaje,

        @Schema(
                description = "Ruta de la solicitud que produjo el error",
                example = "/api/v1/incidentes/11111111-1111-1111-1111-111111111111"
        )
        String ruta,

        @Schema(
                description = """
                        Errores de validación organizados por nombre
                        de campo. Está vacío cuando no corresponde
                        a un error de validación.
                        """,
                example = """
                        {
                          "direccion": "La direccion debe tener entre 5 y 250 caracteres"
                        }
                        """
        )
        Map<String, String> erroresValidacion
) {

    public ErrorResponse {
        erroresValidacion = erroresValidacion == null
                ? Map.of()
                : Map.copyOf(erroresValidacion);
    }

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
                erroresValidacion
        );
    }
}