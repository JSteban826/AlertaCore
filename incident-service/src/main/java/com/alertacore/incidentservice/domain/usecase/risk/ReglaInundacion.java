package com.alertacore.incidentservice.domain.usecase.risk;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.ContextoRiesgo;

import java.util.Objects;

public final class ReglaInundacion
        implements ReglaRiesgo {

    private static final int PUNTOS_INUNDACION = 10;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.tipoIncidente()
                == TipoIncidente.INUNDACION
                ? PUNTOS_INUNDACION
                : 0;
    }
}