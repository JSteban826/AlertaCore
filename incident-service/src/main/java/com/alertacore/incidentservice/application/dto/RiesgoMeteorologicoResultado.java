package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;

import java.time.LocalDateTime;
import java.util.UUID;

public record RiesgoMeteorologicoResultado(
        UUID incidenteId,
        double latitud,
        double longitud,
        double temperaturaCelsius,
        double precipitacionMilimetros,
        double lluviaMilimetros,
        double chubascosMilimetros,
        double velocidadVientoKmH,
        int codigoMeteorologico,
        boolean lluviaIntensa,
        NivelRiesgoMeteorologico nivelRiesgoMeteorologico,
        LocalDateTime fechaConsulta,
        String fuente
) {

    public static RiesgoMeteorologicoResultado desde(
            UUID incidenteId,
            CondicionMeteorologica condicion
    ) {
        return new RiesgoMeteorologicoResultado(
                incidenteId,
                condicion.latitud(),
                condicion.longitud(),
                condicion.temperaturaCelsius(),
                condicion.precipitacionMilimetros(),
                condicion.lluviaMilimetros(),
                condicion.chubascosMilimetros(),
                condicion.velocidadVientoKmH(),
                condicion.codigoMeteorologico(),
                condicion.hayLluviaIntensa(),
                condicion.obtenerNivelRiesgo(),
                condicion.fechaConsulta(),
                condicion.fuente()
        );
    }
}