package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;

import java.util.Objects;

public final class CambiarEstadoIncidenteUseCase {

    private static final String RESPONSABLE_PREDETERMINADO =
            "OPERADOR_API";

    private final IncidenteGateway repositorioIncidente;
    private final HistorialEstadoGateway repositorioHistorial;

    public CambiarEstadoIncidenteUseCase(
            IncidenteGateway repositorioIncidente,
            HistorialEstadoGateway repositorioHistorial
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