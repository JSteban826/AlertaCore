package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoClasificadoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.AsignacionRecursoResultado;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import com.alertacore.incidentservice.domain.model.command.AsignarRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;

import java.util.Objects;
import java.util.UUID;

public final class AsignarRecursoUseCase {

    private static final String RESPONSABLE =
            "SISTEMA_ASIGNACION_RECURSOS";

    private final IncidenteGateway incidenteGateway;
    private final HistorialEstadoGateway historialGateway;
    private final ServicioRecursosGateway recursosGateway;

    public AsignarRecursoUseCase(
            IncidenteGateway incidenteGateway,
            HistorialEstadoGateway historialGateway,
            ServicioRecursosGateway recursosGateway
    ) {
        this.incidenteGateway =
                Objects.requireNonNull(
                        incidenteGateway,
                        "El gateway de incidentes es obligatorio"
                );

        this.historialGateway =
                Objects.requireNonNull(
                        historialGateway,
                        "El gateway de historial es obligatorio"
                );

        this.recursosGateway =
                Objects.requireNonNull(
                        recursosGateway,
                        "El gateway de recursos es obligatorio"
                );
    }

    public AsignacionRecursoResultado ejecutar(
            AsignarRecursoCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de asignacion es obligatorio"
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
                        != EstadoIncidente.CLASIFICADO
        ) {
            throw new IncidenteNoClasificadoException(
                    incidente.getId()
            );
        }

        RecursoDisponible recurso =
                recursosGateway.buscarMasCercano(
                        incidente.getId(),
                        comando.tipoRecurso(),
                        incidente.getUbicacion().getLatitud(),
                        incidente.getUbicacion().getLongitud(),
                        comando.distanciaMaximaKm()
                );

        String idempotencyKey =
                UUID.randomUUID().toString();

        ReservaRecurso reserva =
                recursosGateway.reservar(
                        recurso.recursoId(),
                        incidente.getId(),
                        comando.duracionReservaMinutos(),
                        idempotencyKey
                );

        try {
            EstadoIncidente estadoAnterior =
                    incidente.getEstado();

            incidente.cambiarEstado(
                    EstadoIncidente.RECURSO_ASIGNADO
            );

            Incidente guardado =
                    incidenteGateway.guardar(
                            incidente
                    );

            RegistroHistorialEstado registro =
                    RegistroHistorialEstado.crear(
                            guardado.getId(),
                            estadoAnterior,
                            guardado.getEstado(),
                            "Recurso asignado: "
                                    + recurso.codigo()
                                    + ", reserva: "
                                    + reserva.reservaId(),
                            RESPONSABLE
                    );

            historialGateway.guardar(
                    registro
            );

            return new AsignacionRecursoResultado(
                    guardado.getId(),
                    recurso.recursoId(),
                    reserva.reservaId(),
                    recurso.codigo(),
                    recurso.tipo(),
                    reserva.estadoRecurso(),
                    guardado.getEstado(),
                    recurso.distanciaKm(),
                    recurso.tiempoEstimadoLlegadaMinutos(),
                    reserva.fechaReserva(),
                    reserva.fechaVencimiento()
            );
        } catch (RuntimeException excepcionLocal) {
            compensarReserva(
                    recurso.recursoId(),
                    incidente.getId()
            );

            throw excepcionLocal;
        }
    }

    private void compensarReserva(
            UUID recursoId,
            UUID incidenteId
    ) {
        recursosGateway.liberar(
                recursoId,
                incidenteId,
                "Compensacion por fallo al guardar "
                        + "la asignacion en incident-service"
        );
    }
}