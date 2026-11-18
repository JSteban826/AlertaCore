package com.alertacore.resourceservice.infrastructure.driver_adapter.mapper;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;
import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.DespachoRecursoEntity;
import org.springframework.stereotype.Component;

@Component
public class DespachoPersistenceMapper {

    public DespachoRecursoEntity toEntity(
            DespachoRecurso despacho
    ) {

        DespachoRecursoEntity entity =
                new DespachoRecursoEntity();

        entity.setId(
                despacho.getId()
        );

        entity.setRecursoId(
                despacho.getRecursoId()
        );

        entity.setIncidenteId(
                despacho.getIncidenteId()
        );

        entity.setResponsable(
                despacho.getResponsable()
        );

        entity.setFechaDespacho(
                despacho.getFechaDespacho()
        );

        entity.setFechaLlegada(
                despacho.getFechaLlegada()
        );

        entity.setFechaFinalizacion(
                despacho.getFechaFinalizacion()
        );

        return entity;
    }

    public DespachoRecurso toDomain(
            DespachoRecursoEntity entity
    ) {

        return new DespachoRecurso(
                entity.getId(),
                entity.getRecursoId(),
                entity.getIncidenteId(),
                entity.getResponsable(),
                entity.getFechaDespacho(),
                entity.getFechaLlegada(),
                entity.getFechaFinalizacion()
        );
    }
}