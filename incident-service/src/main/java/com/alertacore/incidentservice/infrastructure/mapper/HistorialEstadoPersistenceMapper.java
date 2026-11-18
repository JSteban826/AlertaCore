package com.alertacore.incidentservice.infrastructure.mapper;

import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.HistorialEstadoIncidenteEntity;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.IncidenteEntity;

import java.util.Objects;

public final class HistorialEstadoPersistenceMapper {

    public HistorialEstadoIncidenteEntity aEntidad(
            RegistroHistorialEstado registro,
            IncidenteEntity incidenteEntity
    ) {
        Objects.requireNonNull(
                registro,
                "El registro de historial es obligatorio"
        );

        Objects.requireNonNull(
                incidenteEntity,
                "La entidad del incidente es obligatoria"
        );

        return new HistorialEstadoIncidenteEntity(
                registro.id(),
                incidenteEntity,
                registro.estadoAnterior(),
                registro.nuevoEstado(),
                registro.motivo(),
                registro.responsable(),
                registro.fechaCambio()
        );
    }

    public RegistroHistorialEstado aDominio(
            HistorialEstadoIncidenteEntity entidad
    ) {
        Objects.requireNonNull(
                entidad,
                "La entidad de historial es obligatoria"
        );

        return new RegistroHistorialEstado(
                entidad.getId(),
                entidad.getIncidente().getId(),
                entidad.getEstadoAnterior(),
                entidad.getNuevoEstado(),
                entidad.getMotivo(),
                entidad.getResponsable(),
                entidad.getFechaCambio()
        );
    }
}