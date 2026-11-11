package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;

import java.util.Objects;

public record ContextoRiesgo(
        TipoIncidente tipoIncidente,
        int cantidadPersonasAfectadas,
        boolean hayPersonasHeridas,
        boolean hayLluviaIntensa,
        boolean accesoDificil
) {

    public ContextoRiesgo {
        Objects.requireNonNull(
                tipoIncidente,
                "El tipo de incidente es obligatorio"
        );

        if (cantidadPersonasAfectadas < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas afectadas no puede ser negativa"
            );
        }
    }
}