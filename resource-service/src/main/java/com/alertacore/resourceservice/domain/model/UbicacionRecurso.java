package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;

import java.time.LocalDateTime;
import java.util.Objects;

public final class UbicacionRecurso {

    private final double latitud;
    private final double longitud;
    private final LocalDateTime fechaRegistro;

    public UbicacionRecurso(
            double latitud,
            double longitud,
            LocalDateTime fechaRegistro
    ) {
        validarLatitud(latitud);
        validarLongitud(longitud);

        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaRegistro = Objects.requireNonNull(
                fechaRegistro,
                "La fecha de registro es obligatoria"
        );
    }

    public static UbicacionRecurso crear(double latitud, double longitud) {
        return new UbicacionRecurso(
                latitud,
                longitud,
                LocalDateTime.now()
        );
    }

    private static void validarLatitud(double latitud) {
        if (!Double.isFinite(latitud) || latitud < -90 || latitud > 90) {
            throw new UbicacionInvalidaException(
                    "La latitud debe estar entre -90 y 90"
            );
        }
    }

    private static void validarLongitud(double longitud) {
        if (!Double.isFinite(longitud) || longitud < -180 || longitud > 180) {
            throw new UbicacionInvalidaException(
                    "La longitud debe estar entre -180 y 180"
            );
        }
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}