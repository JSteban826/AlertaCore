package com.alertacore.resourceservice.infrastructure.persistence.mapper;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.infrastructure.persistence.entity.ReservaRecursoEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaPersistenceMapper {

    public ReservaRecursoEntity toEntity(
            ReservaRecurso reserva
    ) {

        ReservaRecursoEntity entity =
                new ReservaRecursoEntity();

        entity.setId(
                reserva.getId()
        );

        entity.setRecursoId(
                reserva.getRecursoId()
        );

        entity.setIncidenteId(
                reserva.getIncidenteId()
        );

        entity.setClaveIdempotencia(
                reserva.getClaveIdempotencia()
        );

        entity.setFechaReserva(
                reserva.getFechaReserva()
        );

        entity.setFechaVencimiento(
                reserva.getFechaVencimiento()
        );

        entity.setEstado(
                reserva.getEstadoReserva()
                        .name()
        );

        return entity;
    }

    public ReservaRecurso toDomain(
            ReservaRecursoEntity entity
    ) {

        return new ReservaRecurso(
                entity.getId(),
                entity.getRecursoId(),
                entity.getIncidenteId(),
                entity.getClaveIdempotencia(),
                entity.getFechaReserva(),
                entity.getFechaVencimiento(),
                EstadoReserva.valueOf(
                        entity.getEstado()
                )
        );
    }
}