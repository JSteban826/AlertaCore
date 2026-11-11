package com.alertacore.incidentservice.domain.model;

import java.util.Objects;

public final class Ubicacion {

    private final double latitud;
    private final double longitud;
    private final String direccion;

    public Ubicacion(
            double latitud,
            double longitud,
            String direccion
    ) {
        validarLatitud(latitud);
        validarLongitud(longitud);
        validarDireccion(direccion);

        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion.trim();
    }

    private void validarLatitud(double latitud) {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException(
                    "La latitud debe estar entre -90 y 90"
            );
        }
    }

    private void validarLongitud(double longitud) {
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException(
                    "La longitud debe estar entre -180 y 180"
            );
        }
    }

    private void validarDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException(
                    "La direccion no puede estar vacia"
            );
        }

        if (direccion.trim().length() > 250) {
            throw new IllegalArgumentException(
                    "La direccion no puede superar los 250 caracteres"
            );
        }
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof Ubicacion ubicacion)) {
            return false;
        }

        return Double.compare(latitud, ubicacion.latitud) == 0
                && Double.compare(longitud, ubicacion.longitud) == 0
                && direccion.equals(ubicacion.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitud, longitud, direccion);
    }
}