package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.ActualizarUbicacionRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.ActualizarUbicacionRecursoDto;

import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

public class ActualizarUbicacionRecursoUseCase {

    private final RecursoGateway recursoGateway;

    public ActualizarUbicacionRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;
    }

    public ActualizarUbicacionRecursoDto actualizar(
            ActualizarUbicacionRecursoCommand command
    ) {

        Recurso recurso =
                recursoGateway
                        .buscarPorId(
                                command.recursoId()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNoEncontradoException(
                                                command.recursoId()
                                                        .toString()
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

        recursoGateway.guardar(
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