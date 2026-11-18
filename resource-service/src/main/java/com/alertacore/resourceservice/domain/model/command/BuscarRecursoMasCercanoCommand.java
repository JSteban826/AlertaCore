package com.alertacore.resourceservice.domain.model.command;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record BuscarRecursoMasCercanoCommand(
        UUID incidenteId,
        TipoRecurso tipoRecurso,
        double latitudIncidente,
        double longitudIncidente,
        double distanciaMaximaKm
) {

    public BuscarRecursoMasCercanoCommand {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Objects.requireNonNull(
                tipoRecurso,
                "El tipo de recurso es obligatorio"
        );

        validarCoordenadas(latitudIncidente, longitudIncidente);

        if (!Double.isFinite(distanciaMaximaKm)
                || distanciaMaximaKm <= 0) {
            throw new IllegalArgumentException(
                    "La distancia máxima debe ser un número mayor que cero"
            );
        }
    }

    private static void validarCoordenadas(
            double latitud,
            double longitud
    ) {
        if (!Double.isFinite(latitud)
                || latitud < -90
                || latitud > 90) {
            throw new UbicacionInvalidaException(
                    "La latitud del incidente debe estar entre -90 y 90"
            );
        }

        if (!Double.isFinite(longitud)
                || longitud < -180
                || longitud > 180) {
            throw new UbicacionInvalidaException(
                    "La longitud del incidente debe estar entre -180 y 180"
            );
        }
    }
}