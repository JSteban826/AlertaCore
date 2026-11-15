package com.alertacore.incidentservice.domain.exception;

public class ServicioGeocodificacionException
        extends RuntimeException {

    public ServicioGeocodificacionException(
            String mensaje
    ) {
        super(mensaje);
    }

    public ServicioGeocodificacionException(
            String mensaje,
            Throwable causa
    ) {
        super(mensaje, causa);
    }
}