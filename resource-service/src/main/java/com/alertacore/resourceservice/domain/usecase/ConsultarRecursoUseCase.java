package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.util.UUID;

public class ConsultarRecursoUseCase {

    private final RecursoGateway recursoGateway;

    public ConsultarRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway = recursoGateway;
    }

    public RecursoDto consultar(
            UUID recursoId
    ) {

        Recurso recurso =
                recursoGateway.buscarPorId(
                                recursoId
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNoEncontradoException(
                                                recursoId.toString()
                                        )
                        );

        return new RecursoDto(
                recurso.getId(),
                recurso.getCodigo(),
                recurso.getNombre(),
                recurso.getTipo(),
                recurso.getEstado(),
                recurso.getUbicacion()
                        .getLatitud(),
                recurso.getUbicacion()
                        .getLongitud(),
                recurso.getFechaDisponibleDesde(),
                recurso.getFechaUltimaActualizacion()
        );
    }
}