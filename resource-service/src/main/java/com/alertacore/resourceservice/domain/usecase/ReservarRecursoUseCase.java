package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.ReservarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.ReservaRecursoDto;

import com.alertacore.resourceservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.resourceservice.domain.exception.RecursoYaReservadoException;

import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;

import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;

import java.util.Objects;

public class ReservarRecursoUseCase {

    private final RecursoGateway recursoGateway;

    private final ReservaRecursoGateway reservaRecursoGateway;

    public ReservarRecursoUseCase(
            RecursoGateway recursoGateway,
            ReservaRecursoGateway reservaRecursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;

        this.reservaRecursoGateway =
                reservaRecursoGateway;
    }

    public ReservaRecursoDto reservar(
            ReservarRecursoCommand command
    ) {

        Objects.requireNonNull(command);

        var reservaExistente =
                reservaRecursoGateway
                        .buscarPorClaveIdempotencia(
                                command.claveIdempotencia()
                        );

        if (
                reservaExistente.isPresent()
        ) {

            return convertirADto(
                    reservaExistente.get()
            );
        }

        Recurso recurso =
                recursoGateway
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNoDisponibleException(
                                                command.recursoId()
                                                        .toString()
                                        )
                        );

        if (
                !recurso.estaDisponible()
        ) {

            throw new RecursoNoDisponibleException(
                    recurso.getCodigo()
            );
        }

        if (
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
                        .isPresent()
        ) {

            throw new RecursoYaReservadoException();
        }

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        recurso.getId(),
                        command.incidenteId(),
                        command.claveIdempotencia(),
                        command.duracionReservaMinutos()
                );

        recurso.reservar();

        recursoGateway.guardar(
                recurso
        );

        ReservaRecurso reservaGuardada =
                reservaRecursoGateway
                        .guardar(
                                reserva
                        );

        return convertirADto(
                reservaGuardada
        );
    }

    private ReservaRecursoDto convertirADto(
            ReservaRecurso reserva
    ) {

        return new ReservaRecursoDto(
                reserva.getId(),
                reserva.getRecursoId(),
                reserva.getIncidenteId(),
                com.alertacore.resourceservice.domain.enums
                        .EstadoRecurso.RESERVADO,
                reserva.getFechaReserva(),
                reserva.getFechaVencimiento()
        );
    }
}