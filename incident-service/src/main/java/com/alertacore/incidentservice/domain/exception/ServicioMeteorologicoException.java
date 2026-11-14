package com.alertacore.incidentservice.domain.exception;

public class ServicioMeteorologicoException
        extends RuntimeException {

    public ServicioMeteorologicoException(
            String mensaje
    ) {
        super(mensaje);
    }

    public ServicioMeteorologicoException(
            String mensaje,
            Throwable causa
    ) {
        super(mensaje, causa);
    }
}
