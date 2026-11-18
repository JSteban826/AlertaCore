package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.DespachoRecursoDto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoReservadoException;
import com.alertacore.resourceservice.domain.model.DespachoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;

import com.alertacore.resourceservice.domain.model.gateway.DespachoRecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

public class DespacharRecursoUseCase {

    private final RecursoGateway recursoGateway;

    private final DespachoRecursoGateway despachoRecursoGateway;

    public DespacharRecursoUseCase(
            RecursoGateway recursoGateway,
            DespachoRecursoGateway despachoRecursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;

        this.despachoRecursoGateway =
                despachoRecursoGateway;
    }

    public DespachoRecursoDto despachar(
            DespacharRecursoCommand command
    ) {

        Recurso recurso =
                recursoGateway
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNoReservadoException(
                                                command.recursoId()
                                                        .toString()
                                        )
                        );

        if (
                recurso.getEstado()
                        != EstadoRecurso.RESERVADO
        ) {

            throw new RecursoNoReservadoException(
                    recurso.getCodigo()
            );
        }

        recurso.despachar();

        recursoGateway.guardar(
                recurso
        );

        DespachoRecurso despacho =
                DespachoRecurso.crear(
                        recurso.getId(),
                        command.incidenteId(),
                        command.responsable()
                );

        DespachoRecurso despachoGuardado =
                despachoRecursoGateway
                        .guardar(
                                despacho
                        );

        return new DespachoRecursoDto(
                despachoGuardado.getRecursoId(),
                despachoGuardado.getIncidenteId(),
                recurso.getEstado(),
                despachoGuardado.getFechaDespacho()
        );
    }
}