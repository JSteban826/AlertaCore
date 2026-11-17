package com.alertacore.incidentservice.domain.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record EstadisticasIncidentesResultado(
        long totalIncidentes,
        long incidentesValidados,
        long incidentesSinValidar,
        double tiempoPromedioRespuestaMinutos,
        double tiempoMinimoRespuestaMinutos,
        double tiempoMaximoRespuestaMinutos,
        Map<String, Long> cantidadPorEstado,
        Map<String, Long> cantidadPorNivelRiesgo
) {

    public static EstadisticasIncidentesResultado desde(
            EstadisticasIncidentes estadisticas
    ) {
        Map<String, Long> porEstado =
                new LinkedHashMap<>();

        estadisticas.cantidadPorEstado()
                .forEach(
                        (estado, cantidad) ->
                                porEstado.put(
                                        estado.name(),
                                        cantidad
                                )
                );

        Map<String, Long> porNivel =
                new LinkedHashMap<>();

        estadisticas.cantidadPorNivelRiesgo()
                .forEach(
                        (nivel, cantidad) ->
                                porNivel.put(
                                        nivel.name(),
                                        cantidad
                                )
                );

        if (estadisticas.incidentesSinClasificar() > 0) {
            porNivel.put(
                    "SIN_CLASIFICAR",
                    estadisticas.incidentesSinClasificar()
            );
        }

        return new EstadisticasIncidentesResultado(
                estadisticas.totalIncidentes(),
                estadisticas.incidentesValidados(),
                estadisticas.incidentesSinValidar(),
                estadisticas
                        .tiempoPromedioRespuestaMinutos(),
                estadisticas
                        .tiempoMinimoRespuestaMinutos(),
                estadisticas
                        .tiempoMaximoRespuestaMinutos(),
                Map.copyOf(porEstado),
                Map.copyOf(porNivel)
        );
    }
}