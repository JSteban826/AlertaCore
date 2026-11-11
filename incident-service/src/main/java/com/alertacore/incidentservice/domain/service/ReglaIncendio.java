package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;

import java.util.Objects;

public final class ReglaIncendio
        implements ReglaRiesgo {

    private static final int PUNTOS_INCENDIO = 15;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.tipoIncidente()
                == TipoIncidente.INCENDIO
                ? PUNTOS_INCENDIO
                : 0;
    }
}