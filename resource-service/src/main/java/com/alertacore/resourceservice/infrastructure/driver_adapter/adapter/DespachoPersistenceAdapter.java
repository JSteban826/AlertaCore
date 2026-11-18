package com.alertacore.resourceservice.infrastructure.driver_adapter.adapter;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;

import com.alertacore.resourceservice.infrastructure.driver_adapter.mapper.DespachoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.driver_adapter.repository.DespachoRecursoJpaRepository;

import com.alertacore.resourceservice.domain.model.gateway.DespachoRecursoGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DespachoPersistenceAdapter
        implements DespachoRecursoGateway {

    private final DespachoRecursoJpaRepository repository;

    private final DespachoPersistenceMapper mapper;

    @Override
    public DespachoRecurso guardar(
            DespachoRecurso despacho
    ) {

        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(despacho)
                )
        );
    }
}