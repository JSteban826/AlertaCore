package com.alertacore.incidentservice.domain.service;

import java.util.Objects;

public final class ReglaAccesoDificil
        implements ReglaRiesgo {

    private static final int PUNTOS_ACCESO_DIFICIL = 10;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.accesoDificil()
                ? PUNTOS_ACCESO_DIFICIL
                : 0;
    }
}