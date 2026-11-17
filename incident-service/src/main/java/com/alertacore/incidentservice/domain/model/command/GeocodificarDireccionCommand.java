package com.alertacore.incidentservice.domain.model.command;

public record GeocodificarDireccionCommand(
        String direccion
) {

    public GeocodificarDireccionCommand {
        if (direccion == null
                || direccion.isBlank()) {

            throw new IllegalArgumentException(
                    "La direccion es obligatoria"
            );
        }

        direccion = direccion.trim();

        if (direccion.length() < 5) {
            throw new IllegalArgumentException(
                    "La direccion debe tener al menos 5 caracteres"
            );
        }

        if (direccion.length() > 250) {
            throw new IllegalArgumentException(
                    "La direccion no puede superar los 250 caracteres"
            );
        }
    }
}