package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.util.List;

public class BuscarRecursosDisponiblesUseCase {

    private final RecursoGateway recursoGateway;

    public BuscarRecursosDisponiblesUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;
    }

    public List<RecursoDto> buscar(
            TipoRecurso tipo
    ) {

        return recursoGateway
                .buscarDisponiblesPorTipo(tipo)
                .stream()
                .map(
                        recurso ->
                                new RecursoDto(
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
                                )
                )
                .toList();
    }
}