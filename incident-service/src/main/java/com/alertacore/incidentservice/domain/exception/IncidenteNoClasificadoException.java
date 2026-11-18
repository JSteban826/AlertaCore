package com.alertacore.incidentservice.domain.exception;

import java.util.UUID;

public class IncidenteNoClasificadoException
        extends RuntimeException {

    public IncidenteNoClasificadoException(
            UUID incidenteId
    ) {
        super(
                "El incidente debe estar CLASIFICADO "
                        + "antes de asignar un recurso: "
                        + incidenteId
        );
    }
}