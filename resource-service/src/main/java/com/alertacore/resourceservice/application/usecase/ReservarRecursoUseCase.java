package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.ReservarRecursoCommand;
import com.alertacore.resourceservice.application.dto.ReservaRecursoDto;
import com.alertacore.resourceservice.application.port.in.ReservarRecursoPort;
import com.alertacore.resourceservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.resourceservice.domain.exception.RecursoYaReservadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioReservaRecursoPort;

import java.util.Objects;

public class ReservarRecursoUseCase
        implements ReservarRecursoPort {

    private final RepositorioRecursoPort repositorioRecursoPort;

    private final RepositorioReservaRecursoPort
            repositorioReservaRecursoPort;

    public ReservarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort,
            RepositorioReservaRecursoPort repositorioReservaRecursoPort
    ) {

        this.repositorioRecursoPort =
                repositorioRecursoPort;

        this.repositorioReservaRecursoPort =
                repositorioReservaRecursoPort;
    }

    @Override
    public ReservaRecursoDto reservar(
            ReservarRecursoCommand command
    ) {

        Objects.requireNonNull(command);

        var reservaExistentePorIdempotencia =
                repositorioReservaRecursoPort
                        .buscarPorClaveIdempotencia(
                                command.claveIdempotencia()
                        );

        if (reservaExistentePorIdempotencia.isPresent()) {

            return convertirADto(
                    reservaExistentePorIdempotencia.get()
            );
        }

        Recurso recurso =
                repositorioRecursoPort
                        .buscarPorId(command.recursoId())
                        .orElseThrow(() ->
                                new RecursoNoDisponibleException(
                                        command.recursoId().toString()
                                )
                        );

        if (!recurso.estaDisponible()) {

            throw new RecursoNoDisponibleException(
                    recurso.getCodigo()
            );
        }

        if (repositorioReservaRecursoPort
                .buscarReservaActivaPorRecurso(
                        recurso.getId()
                )
                .isPresent()) {

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

        repositorioRecursoPort.guardar(recurso);

        ReservaRecurso reservaGuardada =
                repositorioReservaRecursoPort
                        .guardar(reserva);

        return convertirADto(reservaGuardada);
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