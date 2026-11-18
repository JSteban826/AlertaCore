package com.alertacore.resourceservice.infrastructure.driver_adapter.adapter;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.infrastructure.driver_adapter.mapper.ReservaPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.driver_adapter.repository.ReservaRecursoJpaRepository;

import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class ReservaPersistenceAdapter
        implements ReservaRecursoGateway {

    private final ReservaRecursoJpaRepository repository;

    private final ReservaPersistenceMapper mapper;

    @Override
    public ReservaRecurso guardar(
            ReservaRecurso reserva
    ) {

        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(reserva)
                )
        );
    }

    @Override
    public Optional<ReservaRecurso>
    buscarReservaActivaPorRecurso(
            UUID recursoId
    ) {

        return repository
                .findByRecursoIdAndEstado(
                        recursoId,
                        EstadoReserva.ACTIVA.name()
                )
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ReservaRecurso>
    buscarPorClaveIdempotencia(
            String claveIdempotencia
    ) {

        return repository
                .findByClaveIdempotencia(
                        claveIdempotencia
                )
                .map(mapper::toDomain);
    }
}