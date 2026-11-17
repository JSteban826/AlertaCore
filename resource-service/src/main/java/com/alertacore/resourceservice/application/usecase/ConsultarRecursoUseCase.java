package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.port.in.ConsultarRecursoPort;
import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.util.UUID;

public class ConsultarRecursoUseCase
        implements ConsultarRecursoPort {

    private final RepositorioRecursoPort repositorio;

    public ConsultarRecursoUseCase(
            RepositorioRecursoPort repositorio
    ) {
        this.repositorio = repositorio;
    }

    @Override
    public RecursoDto consultar(UUID recursoId) {

        Recurso recurso =
                repositorio.buscarPorId(recursoId)
                        .orElseThrow(() ->
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
                recurso.getUbicacion().getLatitud(),
                recurso.getUbicacion().getLongitud(),
                recurso.getFechaDisponibleDesde(),
                recurso.getFechaUltimaActualizacion()
        );
    }
}