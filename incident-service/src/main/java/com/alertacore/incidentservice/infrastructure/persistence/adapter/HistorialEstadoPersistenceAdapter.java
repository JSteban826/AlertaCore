package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.infrastructure.persistence.entity.HistorialEstadoIncidenteEntity;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaHistorialEstadoRepository;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class HistorialEstadoPersistenceAdapter
        implements HistorialEstadoGateway {

    private final JpaHistorialEstadoRepository
            repositorioHistorialJpa;

    private final JpaIncidenteRepository
            repositorioIncidenteJpa;

    private final HistorialEstadoPersistenceMapper mapper;

    public HistorialEstadoPersistenceAdapter(
            JpaHistorialEstadoRepository
                    repositorioHistorialJpa,
            JpaIncidenteRepository
                    repositorioIncidenteJpa,
            HistorialEstadoPersistenceMapper mapper
    ) {
        this.repositorioHistorialJpa =
                Objects.requireNonNull(
                        repositorioHistorialJpa,
                        "El repositorio de historial es obligatorio"
                );

        this.repositorioIncidenteJpa =
                Objects.requireNonNull(
                        repositorioIncidenteJpa,
                        "El repositorio de incidentes es obligatorio"
                );

        this.mapper = Objects.requireNonNull(
                mapper,
                "El mapper de historial es obligatorio"
        );
    }

    @Override
    public RegistroHistorialEstado guardar(
            RegistroHistorialEstado registro
    ) {
        Objects.requireNonNull(
                registro,
                "El registro de historial es obligatorio"
        );

        IncidenteEntity incidenteEntity =
                repositorioIncidenteJpa.getReferenceById(
                        registro.incidenteId()
                );

        HistorialEstadoIncidenteEntity entidad =
                mapper.aEntidad(
                        registro,
                        incidenteEntity
                );

        HistorialEstadoIncidenteEntity guardada =
                repositorioHistorialJpa.save(entidad);

        return mapper.aDominio(guardada);
    }

    @Override
    public List<RegistroHistorialEstado>
    buscarPorIncidenteIdOrdenado(
            UUID incidenteId
    ) {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        return repositorioHistorialJpa
                .findByIncidenteIdOrderByFechaCambioAsc(
                        incidenteId
                )
                .stream()
                .map(mapper::aDominio)
                .toList();
    }
}