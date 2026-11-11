package com.alertacore.incidentservice.domain.service;

import java.util.Objects;

public final class ReglaCantidadAfectados
        implements ReglaRiesgo {

    private static final int LIMITE_PERSONAS_AFECTADAS = 10;
    private static final int PUNTOS_CANTIDAD_AFECTADOS = 20;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.cantidadPersonasAfectadas()
                > LIMITE_PERSONAS_AFECTADAS
                ? PUNTOS_CANTIDAD_AFECTADOS
                : 0;
    }
}