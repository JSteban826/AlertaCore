package com.alertacore.resourceservice.domain.model.gateway;

import com.alertacore.resourceservice.domain.model.ReservaRecurso;

import java.util.Optional;
import java.util.UUID;

public interface ReservaRecursoGateway {

    ReservaRecurso guardar(
            ReservaRecurso reserva
    );

    Optional<ReservaRecurso>
    buscarReservaActivaPorRecurso(
            UUID recursoId
    );

    Optional<ReservaRecurso>
    buscarPorClaveIdempotencia(
            String claveIdempotencia
    );
}