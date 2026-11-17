package com.alertacore.resourceservice.domain.port.out;

import com.alertacore.resourceservice.domain.model.ReservaRecurso;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioReservaRecursoPort {

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