package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Pagina;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    public Pagina<Incidente> buscarPaginado(
            EstadoIncidente estado,
            TipoIncidente tipo,
            int numeroPagina,
            int tamanioPagina
    ) {
        PageRequest pageable = PageRequest.of(
                numeroPagina,
                tamanioPagina,
                Sort.by(
                        Sort.Direction.DESC,
                        "fechaReporte"
                )
        );

        org.springframework.data.domain.Page<IncidenteEntity>
                paginaEntidades;

        if (estado != null && tipo != null) {
            paginaEntidades =
                    repositorioJpa.findByEstadoAndTipo(
                            estado,
                            tipo,
                            pageable
                    );
        } else if (estado != null) {
            paginaEntidades =
                    repositorioJpa.findByEstado(
                            estado,
                            pageable
                    );
        } else if (tipo != null) {
            paginaEntidades =
                    repositorioJpa.findByTipo(
                            tipo,
                            pageable
                    );
        } else {
            paginaEntidades =
                    repositorioJpa.findAll(pageable);
        }

        return new Pagina<>(
                paginaEntidades
                        .getContent()
                        .stream()
                        .map(mapper::aDominio)
                        .toList(),
                paginaEntidades.getNumber(),
                paginaEntidades.getSize(),
                paginaEntidades.getTotalElements(),
                paginaEntidades.getTotalPages()
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
    public Optional<Incidente> buscarPorId(
            UUID id
    ) {
        Objects.requireNonNull(
                id,
                "El identificador del incidente es obligatorio"
        );

        return repositorioJpa
                .findById(id)
                .map(mapper::aDominio);
    }

    @Override
    public boolean existePorId(
            UUID id
    ) {
        Objects.requireNonNull(
                id,
                "El identificador del incidente es obligatorio"
        );

        return repositorioJpa.existsById(id);
    }
}