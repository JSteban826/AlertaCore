package com.alertacore.incidentservice.domain.exception;

public class ServicioRecursosException
        extends RuntimeException {

    public ServicioRecursosException(
            String mensaje
    ) {
        super(mensaje);
    }

    public ServicioRecursosException(
            String mensaje,
            Throwable causa
    ) {
        super(
                mensaje,
                causa
        );
    }
}