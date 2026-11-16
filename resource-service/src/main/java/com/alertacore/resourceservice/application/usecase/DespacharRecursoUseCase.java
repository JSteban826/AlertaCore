package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.application.dto.DespachoRecursoDto;
import com.alertacore.resourceservice.application.port.in.DespacharRecursoPort;
import com.alertacore.resourceservice.domain.exception.RecursoNoReservadoException;
import com.alertacore.resourceservice.domain.model.DespachoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioDespachoRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

public class DespacharRecursoUseCase
        implements DespacharRecursoPort {

    private final RepositorioRecursoPort
            repositorioRecursoPort;

    private final RepositorioDespachoRecursoPort
            repositorioDespachoRecursoPort;

    public DespacharRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort,
            RepositorioDespachoRecursoPort repositorioDespachoRecursoPort
    ) {

        this.repositorioRecursoPort =
                repositorioRecursoPort;

        this.repositorioDespachoRecursoPort =
                repositorioDespachoRecursoPort;
    }

    @Override
    public DespachoRecursoDto despachar(
            DespacharRecursoCommand command
    ) {

        Recurso recurso =
                repositorioRecursoPort
                        .buscarPorId(command.recursoId())
                        .orElseThrow(() ->
                                new RecursoNoReservadoException(
                                        command.recursoId()
                                                .toString()
                                )
                        );

        if (recurso.getEstado()
                != com.alertacore.resourceservice.domain.enums
                .EstadoRecurso.RESERVADO) {

            throw new RecursoNoReservadoException(
                    recurso.getCodigo()
            );
        }

        recurso.despachar();

        repositorioRecursoPort
                .guardar(recurso);

        DespachoRecurso despacho =
                DespachoRecurso.crear(
                        recurso.getId(),
                        command.incidenteId(),
                        command.responsable()
                );

        DespachoRecurso despachoGuardado =
                repositorioDespachoRecursoPort
                        .guardar(despacho);

        return new DespachoRecursoDto(
                despachoGuardado.getRecursoId(),
                despachoGuardado.getIncidenteId(),
                recurso.getEstado(),
                despachoGuardado.getFechaDespacho()
        );
    }
}