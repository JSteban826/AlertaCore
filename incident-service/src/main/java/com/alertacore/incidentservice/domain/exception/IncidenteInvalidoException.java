package com.alertacore.incidentservice.domain.exception;

public class IncidenteInvalidoException extends RuntimeException {

    public IncidenteInvalidoException(String mensaje) {
        super(mensaje);
    }
}