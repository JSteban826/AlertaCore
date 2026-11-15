package com.alertacore.resourceservice.domain.exception;

public abstract class DominioException extends RuntimeException {

    private final String codigo;

    protected DominioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}