package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;

import java.time.LocalDateTime;
import java.util.Objects;

public record CondicionMeteorologica(
        double latitud,
        double longitud,
        double temperaturaCelsius,
        double precipitacionMilimetros,
        double lluviaMilimetros,
        double chubascosMilimetros,
        double velocidadVientoKmH,
        int codigoMeteorologico,
        LocalDateTime fechaConsulta,
        String fuente
) {

    private static final double LIMITE_MEDIO_MM = 2.5;
    private static final double LIMITE_ALTO_MM = 7.5;
    private static final double LIMITE_CRITICO_MM = 15.0;

    public CondicionMeteorologica {
        validarLatitud(latitud);
        validarLongitud(longitud);

        if (precipitacionMilimetros < 0) {
            throw new IllegalArgumentException(
                    "La precipitacion no puede ser negativa"
            );
        }

        if (lluviaMilimetros < 0) {
            throw new IllegalArgumentException(
                    "La lluvia no puede ser negativa"
            );
        }

        if (chubascosMilimetros < 0) {
            throw new IllegalArgumentException(
                    "Los chubascos no pueden ser negativos"
            );
        }

        if (velocidadVientoKmH < 0) {
            throw new IllegalArgumentException(
                    "La velocidad del viento no puede ser negativa"
            );
        }

        Objects.requireNonNull(
                fechaConsulta,
                "La fecha de consulta es obligatoria"
        );

        if (fuente == null || fuente.isBlank()) {
            throw new IllegalArgumentException(
                    "La fuente meteorologica es obligatoria"
            );
        }

        fuente = fuente.trim();
    }

    public boolean hayLluviaIntensa() {
        return precipitacionMilimetros >= LIMITE_ALTO_MM;
    }

    public NivelRiesgoMeteorologico obtenerNivelRiesgo() {
        if (precipitacionMilimetros < LIMITE_MEDIO_MM) {
            return NivelRiesgoMeteorologico.BAJO;
        }

        if (precipitacionMilimetros < LIMITE_ALTO_MM) {
            return NivelRiesgoMeteorologico.MEDIO;
        }

        if (precipitacionMilimetros < LIMITE_CRITICO_MM) {
            return NivelRiesgoMeteorologico.ALTO;
        }

        return NivelRiesgoMeteorologico.CRITICO;
    }

    private static void validarLatitud(double latitud) {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException(
                    "La latitud debe estar entre -90 y 90"
            );
        }
    }

    private static void validarLongitud(double longitud) {
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException(
                    "La longitud debe estar entre -180 y 180"
            );
        }
    }
}