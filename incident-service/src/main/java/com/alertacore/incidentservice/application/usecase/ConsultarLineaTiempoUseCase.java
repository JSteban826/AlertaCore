package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.application.port.in.ConsultarLineaTiempoPort;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.port.out.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ConsultarLineaTiempoUseCase
        implements ConsultarLineaTiempoPort {

    private final RepositorioIncidentePort repositorioIncidente;
    private final RepositorioHistorialEstadoPort repositorioHistorial;

    public ConsultarLineaTiempoUseCase(
            RepositorioIncidentePort repositorioIncidente,
            RepositorioHistorialEstadoPort repositorioHistorial
    ) {
        this.repositorioIncidente = Objects.requireNonNull(
                repositorioIncidente,
                "El repositorio de incidentes es obligatorio"
        );

        this.repositorioHistorial = Objects.requireNonNull(
                repositorioHistorial,
                "El repositorio de historial es obligatorio"
        );
    }

    @Override
    public List<RegistroLineaTiempoResultado> ejecutar(
            UUID incidenteId
    ) {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        if (!repositorioIncidente.existePorId(incidenteId)) {
            throw new IncidenteNoEncontradoException(
                    incidenteId
            );
        }

        return repositorioHistorial
                .buscarPorIncidenteIdOrdenado(incidenteId)
                .stream()
                .map(RegistroLineaTiempoResultado::desde)
                .toList();
    }
}