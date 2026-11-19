package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.domain.model.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.LiberarRecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;

import java.time.LocalDateTime;
import java.util.Objects;

public final class LiberarRecursoUseCase {

    private final RecursoGateway recursoGateway;

    private final ReservaRecursoGateway
            reservaRecursoGateway;

    public LiberarRecursoUseCase(
            RecursoGateway recursoGateway,
            ReservaRecursoGateway reservaRecursoGateway
    ) {
        this.recursoGateway =
                Objects.requireNonNull(
                        recursoGateway,
                        "El gateway de recursos es obligatorio"
                );

        this.reservaRecursoGateway =
                Objects.requireNonNull(
                        reservaRecursoGateway,
                        "El gateway de reservas es obligatorio"
                );
    }

    public LiberarRecursoDto liberar(
            LiberarRecursoCommand command
    ) {
        Objects.requireNonNull(
                command,
                "El comando de liberacion es obligatorio"
        );

        Recurso recurso =
                recursoGateway
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNoLiberableException(
                                                command.recursoId()
                                                        .toString()
                                        )
                        );

        /*
         * Diagnóstico temporal del recurso reconstruido
         * desde PostgreSQL.
         */
        System.out.println(
                "DEBUG recursoId: "
                        + recurso.getId()
        );

        System.out.println(
                "DEBUG codigo recurso: "
                        + recurso.getCodigo()
        );

        System.out.println(
                "DEBUG estado recurso: "
                        + recurso.getEstado()
        );

        System.out.println(
                "DEBUG incidente recibido: "
                        + command.incidenteId()
        );

        if (
                recurso.getEstado()
                        != EstadoRecurso.DESPACHADO
                        &&
                        recurso.getEstado()
                                != EstadoRecurso.EN_ATENCION
        ) {
            System.out.println(
                    "DEBUG rechazo: ESTADO_RECURSO_NO_LIBERABLE"
            );

            throw new RecursoNoLiberableException(
                    recurso.getCodigo()
            );
        }

        ReservaRecurso reserva =
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
                        .orElseThrow(
                                () -> {
                                    System.out.println(
                                            "DEBUG rechazo: "
                                                    + "RESERVA_ACTIVA_NO_ENCONTRADA"
                                    );

                                    return new RecursoNoLiberableException(
                                            recurso.getCodigo()
                                    );
                                }
                        );

        /*
         * Diagnóstico temporal de la reserva reconstruida.
         */
        System.out.println(
                "DEBUG reservaId: "
                        + reserva.getId()
        );

        System.out.println(
                "DEBUG estado reserva: "
                        + reserva.getEstadoReserva()
        );

        System.out.println(
                "DEBUG incidente reserva: "
                        + reserva.getIncidenteId()
        );

        if (
                !reserva.getIncidenteId()
                        .equals(
                                command.incidenteId()
                        )
        ) {
            System.out.println(
                    "DEBUG rechazo: INCIDENTE_NO_COINCIDE"
            );

            throw new RecursoNoLiberableException(
                    recurso.getCodigo()
            );
        }

        /*
         * La reserva deja de estar activa antes de que
         * el recurso vuelva a estar disponible.
         */
        reserva.cancelar();

        recurso.liberar();

        System.out.println(
                "DEBUG nuevo estado reserva: "
                        + reserva.getEstadoReserva()
        );

        System.out.println(
                "DEBUG nuevo estado recurso: "
                        + recurso.getEstado()
        );

        reservaRecursoGateway.guardar(
                reserva
        );

        recursoGateway.guardar(
                recurso
        );

        LocalDateTime fechaLiberacion =
                LocalDateTime.now();

        return new LiberarRecursoDto(
                recurso.getId(),
                recurso.getEstado(),
                fechaLiberacion
        );
    }
}