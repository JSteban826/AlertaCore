package com.alertacore.resourceservice.application.command;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;

import java.util.Objects;

public record RegistrarRecursoCommand(
        String codigo,
        String nombre,
        TipoRecurso tipo,
        double latitud,
        double longitud
) {

    public RegistrarRecursoCommand {
        codigo = validarTexto(
                codigo,
                "El código del recurso es obligatorio"
        );

        nombre = validarTexto(
                nombre,
                "El nombre del recurso es obligatorio"
        );

        Objects.requireNonNull(
                tipo,
                "El tipo del recurso es obligatorio"
        );

        validarCoordenadas(latitud, longitud);
    }

    private static String validarTexto(
            String valor,
            String mensaje
    ) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }

        return valor.trim();
    }

    private static void validarCoordenadas(
            double latitud,
            double longitud
    ) {
        if (!Double.isFinite(latitud)
                || latitud < -90
                || latitud > 90) {
            throw new UbicacionInvalidaException(
                    "La latitud debe estar entre -90 y 90"
            );
        }

        if (!Double.isFinite(longitud)
                || longitud < -180
                || longitud > 180) {
            throw new UbicacionInvalidaException(
                    "La longitud debe estar entre -180 y 180"
            );
        }
    }
}