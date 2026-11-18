package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.util.List;

public class ListarRecursosUseCase {

    private final RecursoGateway recursoGateway;

    public ListarRecursosUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;
    }

    public List<RecursoDto> listar() {

        return recursoGateway
                .buscarTodos()
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