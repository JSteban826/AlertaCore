package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.application.port.in.ConsultarEstadisticasIncidentesPort;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.domain.model.gateway.EstadisticasIncidenteGateway;

import java.util.Objects;

public final class ConsultarEstadisticasIncidentesUseCase
        implements ConsultarEstadisticasIncidentesPort {

    private final EstadisticasIncidenteGateway repositorio;

    public ConsultarEstadisticasIncidentesUseCase(
            EstadisticasIncidenteGateway repositorio
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de estadisticas es obligatorio"
        );
    }

    @Override
    public EstadisticasIncidentesResultado ejecutar() {
        EstadisticasIncidentes estadisticas =
                repositorio.obtenerEstadisticas();

        return EstadisticasIncidentesResultado.desde(
                estadisticas
        );
    }
}