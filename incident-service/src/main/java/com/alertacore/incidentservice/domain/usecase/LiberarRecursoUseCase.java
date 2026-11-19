package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.exception
        .IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.LiberacionRecurso;
import com.alertacore.incidentservice.domain.model
        .LiberacionRecursoResultado;
import com.alertacore.incidentservice.domain.model.command
        .LiberarRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway
        .IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway
        .ServicioRecursosGateway;

import java.util.Objects;

public final class LiberarRecursoUseCase {

    private final IncidenteGateway incidenteGateway;
    private final ServicioRecursosGateway recursosGateway;

    public LiberarRecursoUseCase(
            IncidenteGateway incidenteGateway,
            ServicioRecursosGateway recursosGateway
    ) {
        this.incidenteGateway =
                Objects.requireNonNull(
                        incidenteGateway,
                        "El gateway de incidentes es obligatorio"
                );

        this.recursosGateway =
                Objects.requireNonNull(
                        recursosGateway,
                        "El gateway de recursos es obligatorio"
                );
    }

    public LiberacionRecursoResultado ejecutar(
            LiberarRecursoCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de liberacion es obligatorio"
        );

        Incidente incidente =
                incidenteGateway.buscarPorId(
                                comando.incidenteId()
                        )
                        .orElseThrow(
                                () ->
                                        new IncidenteNoEncontradoException(
                                                comando.incidenteId()
                                        )
                        );

        if (
                incidente.getEstado()
                        != EstadoIncidente.RECURSO_ASIGNADO
        ) {
            throw new IllegalStateException(
                    "El incidente no tiene un recurso asignado"
            );
        }

        LiberacionRecurso liberacion =
                recursosGateway.liberar(
                        comando.recursoId(),
                        comando.incidenteId(),
                        comando.motivo()
                );

        if (
                liberacion.estado()
                        != EstadoRecurso.DISPONIBLE
        ) {
            throw new IllegalStateException(
                    "Resource Service no confirmo "
                            + "la liberacion del recurso"
            );
        }

        return new LiberacionRecursoResultado(
                incidente.getId(),
                liberacion.recursoId(),
                liberacion.estado(),
                incidente.getEstado(),
                liberacion.fechaLiberacion()
        );
    }
}