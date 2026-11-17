package com.alertacore.resourceservice.infrastructure.persistence.adapter;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioDespachoRecursoPort;
import com.alertacore.resourceservice.infrastructure.persistence.mapper.DespachoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.persistence.repository.DespachoRecursoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DespachoPersistenceAdapter
        implements RepositorioDespachoRecursoPort {

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