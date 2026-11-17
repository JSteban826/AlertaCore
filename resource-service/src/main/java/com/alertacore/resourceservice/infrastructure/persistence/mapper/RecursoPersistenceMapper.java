package com.alertacore.resourceservice.infrastructure.persistence.mapper;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.infrastructure.persistence.entity.RecursoEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RecursoPersistenceMapper {

    public RecursoEntity toEntity(
            Recurso recurso
    ) {

        RecursoEntity entity =
                new RecursoEntity();

        entity.setId(
                recurso.getId()
        );

        entity.setCodigo(
                recurso.getCodigo()
        );

        entity.setNombre(
                recurso.getNombre()
        );

        entity.setTipo(
                recurso.getTipo().name()
        );

        entity.setEstado(
                recurso.getEstado().name()
        );

        entity.setLatitud(
                recurso.getUbicacion()
                        .getLatitud()
        );

        entity.setLongitud(
                recurso.getUbicacion()
                        .getLongitud()
        );

        entity.setFechaDisponibleDesde(
                recurso.getFechaDisponibleDesde()
        );

        entity.setFechaUltimaActualizacion(
                recurso.getFechaUltimaActualizacion()
        );

        return entity;
    }

    public Recurso toDomain(
            RecursoEntity entity
    ) {

        return new Recurso(
                entity.getId(),
                entity.getCodigo(),
                entity.getNombre(),
                TipoRecurso.valueOf(
                        entity.getTipo()
                ),
                EstadoRecurso.valueOf(
                        entity.getEstado()
                ),
                new UbicacionRecurso(
                        entity.getLatitud(),
                        entity.getLongitud(),
                        entity.getFechaUltimaActualizacion()
                                != null
                                ? entity.getFechaUltimaActualizacion()
                                : LocalDateTime.now()
                ),
                entity.getFechaDisponibleDesde(),
                entity.getFechaUltimaActualizacion()
        );
    }

    public RecursoEntity actualizarEntidad(
            Recurso recurso,
            RecursoEntity entity
    ) {

        entity.setCodigo(
                recurso.getCodigo()
        );

        entity.setNombre(
                recurso.getNombre()
        );

        entity.setTipo(
                recurso.getTipo().name()
        );

        entity.setEstado(
                recurso.getEstado().name()
        );

        entity.setLatitud(
                recurso.getUbicacion().getLatitud()
        );

        entity.setLongitud(
                recurso.getUbicacion().getLongitud()
        );

        entity.setFechaDisponibleDesde(
                recurso.getFechaDisponibleDesde()
        );

        entity.setFechaUltimaActualizacion(
                recurso.getFechaUltimaActualizacion()
        );

        return entity;
    }
}