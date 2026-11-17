package com.alertacore.resourceservice.domain.service;

import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;

public final class CalculadorDistanciaHaversine {

    private static final double RADIO_TIERRA_KM = 6371.0;

    public double calcularKilometros(
            double latitudOrigen,
            double longitudOrigen,
            double latitudDestino,
            double longitudDestino
    ) {
        validarCoordenadas(latitudOrigen, longitudOrigen);
        validarCoordenadas(latitudDestino, longitudDestino);

        double latitudOrigenRadianes = Math.toRadians(latitudOrigen);
        double latitudDestinoRadianes = Math.toRadians(latitudDestino);

        double diferenciaLatitud =
                Math.toRadians(latitudDestino - latitudOrigen);

        double diferenciaLongitud =
                Math.toRadians(longitudDestino - longitudOrigen);

        double haversine =
                Math.pow(Math.sin(diferenciaLatitud / 2), 2)
                        + Math.cos(latitudOrigenRadianes)
                        * Math.cos(latitudDestinoRadianes)
                        * Math.pow(Math.sin(diferenciaLongitud / 2), 2);

        /*
         * Math.min protege contra pequeños errores de precisión
         * que podrían hacer que haversine sea ligeramente mayor que 1.
         */
        double anguloCentral = 2 * Math.asin(
                Math.sqrt(Math.min(1.0, haversine))
        );

        return RADIO_TIERRA_KM * anguloCentral;
    }

    private void validarCoordenadas(
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