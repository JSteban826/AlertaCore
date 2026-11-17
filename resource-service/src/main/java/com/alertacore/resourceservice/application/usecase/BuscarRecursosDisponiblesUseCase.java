package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.port.in.BuscarRecursosDisponiblesPort;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.util.List;

public class BuscarRecursosDisponiblesUseCase
        implements BuscarRecursosDisponiblesPort {

    private final RepositorioRecursoPort repositorio;

    public BuscarRecursosDisponiblesUseCase(
            RepositorioRecursoPort repositorio
    ) {
        this.repositorio = repositorio;
    }

    @Override
    public List<RecursoDto> buscar(
            TipoRecurso tipo
    ) {

        return repositorio
                .buscarDisponiblesPorTipo(tipo)
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