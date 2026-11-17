package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.ConsultarIncidentePort;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;

import java.util.Objects;
import java.util.UUID;

public final class ConsultarIncidenteUseCase
        implements ConsultarIncidentePort {

    private final IncidenteGateway repositorio;

    public ConsultarIncidenteUseCase(
            IncidenteGateway repositorio
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de incidentes es obligatorio"
        );
    }

    @Override
    public ResultadoIncidente ejecutar(
            UUID incidenteId
    ) {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Incidente incidente = repositorio
                .buscarPorId(incidenteId)
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                incidenteId
                        )
                );

        return ResultadoIncidente.desde(incidente);
    }
}