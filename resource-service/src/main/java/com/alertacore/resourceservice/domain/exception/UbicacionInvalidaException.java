package com.alertacore.resourceservice.domain.exception;

public class UbicacionInvalidaException extends DominioException {

    private static final String CODIGO = "UBICACION_INVALIDA";

    public UbicacionInvalidaException(String mensaje) {
        super(CODIGO, mensaje);
    }
}
