package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.application.port.in.ConsultarEstadisticasIncidentesPort;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.domain.port.out.RepositorioEstadisticasIncidentePort;

import java.util.Objects;

public final class ConsultarEstadisticasIncidentesUseCase
        implements ConsultarEstadisticasIncidentesPort {

    private final RepositorioEstadisticasIncidentePort repositorio;

    public ConsultarEstadisticasIncidentesUseCase(
            RepositorioEstadisticasIncidentePort repositorio
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