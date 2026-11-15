package com.alertacore.incidentservice.domain.model;

import java.util.Objects;

public record ResultadoGeocodificacion(
        String direccionConsultada,
        String direccionNormalizada,
        double latitud,
        double longitud,
        String fuente,
        String atribucion
) {

    public ResultadoGeocodificacion {
        validarTexto(
                direccionConsultada,
                "La direccion consultada es obligatoria"
        );

        validarTexto(
                direccionNormalizada,
                "La direccion normalizada es obligatoria"
        );

        validarCoordenadas(
                latitud,
                longitud
        );

        validarTexto(
                fuente,
                "La fuente es obligatoria"
        );

        validarTexto(
                atribucion,
                "La atribucion es obligatoria"
        );

        direccionConsultada =
                direccionConsultada.trim();

        direccionNormalizada =
                direccionNormalizada.trim();

        fuente = fuente.trim();
        atribucion = atribucion.trim();
    }

    private static void validarCoordenadas(
            double latitud,
            double longitud
    ) {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException(
                    "La latitud debe estar entre -90 y 90"
            );
        }

        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException(
                    "La longitud debe estar entre -180 y 180"
            );
        }
    }

    private static void validarTexto(
            String valor,
            String mensaje
    ) {
        Objects.requireNonNull(valor, mensaje);

        if (valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}