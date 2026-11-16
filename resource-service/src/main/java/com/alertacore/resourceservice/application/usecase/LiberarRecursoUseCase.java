package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.application.dto.LiberarRecursoDto;
import com.alertacore.resourceservice.application.port.in.LiberarRecursoPort;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.time.LocalDateTime;

public class LiberarRecursoUseCase
        implements LiberarRecursoPort {

    private final RepositorioRecursoPort
            repositorioRecursoPort;

    public LiberarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {

        this.repositorioRecursoPort =
                repositorioRecursoPort;
    }

    @Override
    public LiberarRecursoDto liberar(
            LiberarRecursoCommand command
    ) {

        Recurso recurso =
                repositorioRecursoPort
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(() ->
                                new RecursoNoLiberableException(
                                        command.recursoId()
                                                .toString()
                                )
                        );

        if (recurso.getEstado()
                != EstadoRecurso.DESPACHADO
                &&
                recurso.getEstado()
                        != EstadoRecurso.EN_ATENCION) {

            throw new RecursoNoLiberableException(
                    recurso.getCodigo()
            );
        }

        recurso.liberar();

        repositorioRecursoPort.guardar(recurso);

        return new LiberarRecursoDto(

                recurso.getId(),

                recurso.getEstado(),

                LocalDateTime.now()
        );
    }
}