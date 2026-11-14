package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;

import java.util.Objects;

public final class CambiarEstadoIncidenteUseCase
        implements CambiarEstadoIncidentePort {

    private final RepositorioIncidentePort repositorio;

    public CambiarEstadoIncidenteUseCase(
            RepositorioIncidentePort repositorio
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de incidentes es obligatorio"
        );
    }

    @Override
    public ResultadoIncidente ejecutar(
            CambiarEstadoIncidenteCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de cambio de estado es obligatorio"
        );

        Incidente incidente = repositorio
                .buscarPorId(comando.incidenteId())
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                comando.incidenteId()
                        )
                );

        incidente.cambiarEstado(
                comando.nuevoEstado()
        );

        Incidente incidenteGuardado =
                repositorio.guardar(incidente);

        return ResultadoIncidente.desde(
                incidenteGuardado
        );
    }
}