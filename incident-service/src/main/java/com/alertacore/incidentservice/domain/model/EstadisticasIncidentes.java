package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;

import java.util.Map;
import java.util.Objects;

public record EstadisticasIncidentes(
        long totalIncidentes,
        long incidentesValidados,
        long incidentesSinValidar,
        double tiempoPromedioRespuestaMinutos,
        double tiempoMinimoRespuestaMinutos,
        double tiempoMaximoRespuestaMinutos,
        Map<EstadoIncidente, Long> cantidadPorEstado,
        Map<NivelRiesgo, Long> cantidadPorNivelRiesgo,
        long incidentesSinClasificar
) {

    public EstadisticasIncidentes {
        if (totalIncidentes < 0) {
            throw new IllegalArgumentException(
                    "El total de incidentes no puede ser negativo"
            );
        }

        if (incidentesValidados < 0) {
            throw new IllegalArgumentException(
                    "Los incidentes validados no pueden ser negativos"
            );
        }

        if (incidentesSinValidar < 0) {
            throw new IllegalArgumentException(
                    "Los incidentes sin validar no pueden ser negativos"
            );
        }

        Objects.requireNonNull(
                cantidadPorEstado,
                "Las cantidades por estado son obligatorias"
        );

        Objects.requireNonNull(
                cantidadPorNivelRiesgo,
                "Las cantidades por nivel son obligatorias"
        );

        cantidadPorEstado =
                Map.copyOf(cantidadPorEstado);

        cantidadPorNivelRiesgo =
                Map.copyOf(cantidadPorNivelRiesgo);
    }

    public static EstadisticasIncidentes vacias() {
        return new EstadisticasIncidentes(
                0,
                0,
                0,
                0,
                0,
                0,
                Map.of(),
                Map.of(),
                0
        );
    }
}