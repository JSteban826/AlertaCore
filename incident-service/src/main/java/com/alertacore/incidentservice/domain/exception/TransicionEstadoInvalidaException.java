package com.alertacore.incidentservice.domain.exception;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;

public class TransicionEstadoInvalidaException
        extends RuntimeException {

    public TransicionEstadoInvalidaException(
            EstadoIncidente estadoActual,
            EstadoIncidente nuevoEstado
    ) {
        super(
                "No es posible cambiar el incidente de "
                        + estadoActual
                        + " a "
                        + nuevoEstado
        );
    }
}