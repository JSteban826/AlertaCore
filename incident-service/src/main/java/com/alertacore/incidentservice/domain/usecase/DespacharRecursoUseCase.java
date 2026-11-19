package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.exception.IncidenteNoClasificadoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.DespachoRecurso;
import com.alertacore.incidentservice.domain.model.DespachoRecursoResultado;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.command.DespacharRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;

import java.util.Objects;

public final class DespacharRecursoUseCase {

    private final IncidenteGateway incidenteGateway;
    private final ServicioRecursosGateway recursosGateway;

    public DespacharRecursoUseCase(
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

    public DespachoRecursoResultado ejecutar(
            DespacharRecursoCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de despacho es obligatorio"
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
            throw new IncidenteNoClasificadoException(
                    incidente.getId()
            );
        }

        DespachoRecurso despacho =
                recursosGateway.despachar(
                        comando.recursoId(),
                        comando.incidenteId(),
                        comando.responsable()
                );

        if (
                despacho.estado()
                        != EstadoRecurso.DESPACHADO
        ) {
            throw new IllegalStateException(
                    "Resource Service no confirmo "
                            + "el despacho del recurso"
            );
        }

        return DespachoRecursoResultado.desde(
                despacho,
                incidente.getEstado()
        );
    }
}