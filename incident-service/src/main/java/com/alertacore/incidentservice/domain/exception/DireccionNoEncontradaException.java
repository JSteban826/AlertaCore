package com.alertacore.incidentservice.domain.exception;

public class DireccionNoEncontradaException
        extends RuntimeException {

    public DireccionNoEncontradaException(
            String direccion
    ) {
        super(
                "No se encontraron coordenadas para la direccion: "
                        + direccion
        );
    }
}