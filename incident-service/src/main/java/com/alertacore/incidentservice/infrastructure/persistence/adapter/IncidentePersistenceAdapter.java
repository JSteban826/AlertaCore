package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class IncidentePersistenceAdapter
        implements RepositorioIncidentePort {

    private final JpaIncidenteRepository repositorioJpa;
    private final IncidentePersistenceMapper mapper;

    public IncidentePersistenceAdapter(
            JpaIncidenteRepository repositorioJpa,
            IncidentePersistenceMapper mapper
    ) {
        this.repositorioJpa = Objects.requireNonNull(
                repositorioJpa,
                "El repositorio JPA es obligatorio"
        );

        this.mapper = Objects.requireNonNull(
                mapper,
                "El mapper de persistencia es obligatorio"
        );
    }

    @Override
    public Incidente guardar(
            Incidente incidente
    ) {
        Objects.requireNonNull(
                incidente,
                "El incidente es obligatorio"
        );

        IncidenteEntity entidad = repositorioJpa
                .findById(incidente.getId())
                .map(entidadExistente -> {
                    mapper.actualizarEntidad(
                            entidadExistente,
                            incidente
                    );

                    return entidadExistente;
                })
                .orElseGet(
                        () -> mapper.aEntidad(incidente)
                );

        IncidenteEntity entidadGuardada =
                repositorioJpa.save(entidad);

        return mapper.aDominio(entidadGuardada);
    }

    @Override
    public Optional<Incidente> buscarPorId(UUID id) {
        Objects.requireNonNull(
                id,
                "El identificador es obligatorio"
        );

        return repositorioJpa
                .findById(id)
                .map(mapper::aDominio);
    }

    @Override
    public boolean existePorId(UUID id) {
        Objects.requireNonNull(
                id,
                "El identificador es obligatorio"
        );

        return repositorioJpa.existsById(id);
    }
}