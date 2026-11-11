package com.alertacore.incidentservice.domain.exception;

import java.util.UUID;

public class IncidenteNoEncontradoException
        extends RuntimeException {

    public IncidenteNoEncontradoException(UUID id) {
        super("No se encontro el incidente con id: " + id);
    }
}