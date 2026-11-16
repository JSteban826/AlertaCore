package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.ActualizarUbicacionRecursoCommand;
import com.alertacore.resourceservice.application.dto.ActualizarUbicacionRecursoDto;
import com.alertacore.resourceservice.application.port.in.ActualizarUbicacionRecursoPort;
import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

public class ActualizarUbicacionRecursoUseCase
        implements ActualizarUbicacionRecursoPort {

    private final RepositorioRecursoPort
            repositorioRecursoPort;

    public ActualizarUbicacionRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {

        this.repositorioRecursoPort =
                repositorioRecursoPort;
    }

    @Override
    public ActualizarUbicacionRecursoDto actualizar(
            ActualizarUbicacionRecursoCommand command
    ) {

        Recurso recurso =
                repositorioRecursoPort
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        command.recursoId().toString()
                                )
                        );

        UbicacionRecurso nuevaUbicacion =
                UbicacionRecurso.crear(
                        command.latitud(),
                        command.longitud()
                );

        recurso.actualizarUbicacion(
                nuevaUbicacion
        );

        repositorioRecursoPort.guardar(
                recurso
        );

        return new ActualizarUbicacionRecursoDto(

                recurso.getId(),

                nuevaUbicacion.getLatitud(),

                nuevaUbicacion.getLongitud(),

                recurso.getFechaUltimaActualizacion()
        );
    }
}