package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.port.out.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;

import java.util.Objects;

public final class CambiarEstadoIncidenteUseCase
        implements CambiarEstadoIncidentePort {

    private static final String RESPONSABLE_PREDETERMINADO =
            "OPERADOR_API";

    private final RepositorioIncidentePort repositorioIncidente;
    private final RepositorioHistorialEstadoPort repositorioHistorial;

    public CambiarEstadoIncidenteUseCase(
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
    public ResultadoIncidente ejecutar(
            CambiarEstadoIncidenteCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de cambio de estado es obligatorio"
        );

        Incidente incidente = repositorioIncidente
                .buscarPorId(comando.incidenteId())
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                comando.incidenteId()
                        )
                );

        EstadoIncidente estadoAnterior =
                incidente.getEstado();

        incidente.cambiarEstado(
                comando.nuevoEstado()
        );

        Incidente incidenteGuardado =
                repositorioIncidente.guardar(incidente);

        RegistroHistorialEstado registro =
                RegistroHistorialEstado.crear(
                        incidenteGuardado.getId(),
                        estadoAnterior,
                        incidenteGuardado.getEstado(),
                        comando.motivo(),
                        RESPONSABLE_PREDETERMINADO
                );

        repositorioHistorial.guardar(registro);

        return ResultadoIncidente.desde(
                incidenteGuardado
        );
    }
}