package com.alertacore.resourceservice.infrastructure.persistence.adapter;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;
import com.alertacore.resourceservice.infrastructure.persistence.mapper.RecursoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.persistence.repository.RecursoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RecursoPersistenceAdapter
        implements RepositorioRecursoPort {

    private final RecursoJpaRepository repository;

    private final RecursoPersistenceMapper mapper;

    @Override
    public Recurso guardar(
            Recurso recurso
    ) {

        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(recurso)
                )
        );
    }

    @Override
    public Optional<Recurso> buscarPorId(
            UUID id
    ) {

        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Recurso> buscarPorCodigo(
            String codigo
    ) {

        return repository.findByCodigo(codigo)
                .map(mapper::toDomain);
    }

    @Override
    public List<Recurso> buscarDisponiblesPorTipo(
            TipoRecurso tipo
    ) {

        return repository
                .findByTipoAndEstado(
                        tipo.name(),
                        EstadoRecurso.DISPONIBLE.name()
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorCodigo(
            String codigo
    ) {

        return repository.existsByCodigo(
                codigo
        );
    }

    @Override
    public List<Recurso> buscarTodos() {

        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}