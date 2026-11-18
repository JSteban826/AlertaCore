package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.LiberarRecursoDto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.time.LocalDateTime;

public class LiberarRecursoUseCase {

    private final RecursoGateway recursoGateway;

    public LiberarRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;
    }

    public LiberarRecursoDto liberar(
            LiberarRecursoCommand command
    ) {

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

        if (
                recurso.getEstado()
                        != EstadoRecurso.DESPACHADO
                        &&
                        recurso.getEstado()
                                != EstadoRecurso.EN_ATENCION
        ) {

            throw new RecursoNoLiberableException(
                    recurso.getCodigo()
            );
        }

        recurso.liberar();

        recursoGateway.guardar(
                recurso
        );

        return new LiberarRecursoDto(
                recurso.getId(),
                recurso.getEstado(),
                LocalDateTime.now()
        );
    }
}