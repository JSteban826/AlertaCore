package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.port.in.ListarRecursosPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.util.List;

public class ListarRecursosUseCase
        implements ListarRecursosPort {

    private final RepositorioRecursoPort repositorio;

    public ListarRecursosUseCase(
            RepositorioRecursoPort repositorio
    ) {
        this.repositorio = repositorio;
    }

    @Override
    public List<RecursoDto> listar() {

        return repositorio.buscarTodos()
                .stream()
                .map(recurso ->
                        new RecursoDto(
                                recurso.getId(),
                                recurso.getCodigo(),
                                recurso.getNombre(),
                                recurso.getTipo(),
                                recurso.getEstado(),
                                recurso.getUbicacion().getLatitud(),
                                recurso.getUbicacion().getLongitud(),
                                recurso.getFechaDisponibleDesde(),
                                recurso.getFechaUltimaActualizacion()
                        )
                )
                .toList();
    }
}