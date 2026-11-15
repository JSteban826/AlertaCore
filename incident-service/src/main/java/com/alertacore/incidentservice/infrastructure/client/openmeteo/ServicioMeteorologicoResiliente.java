package com.alertacore.incidentservice.infrastructure.client.openmeteo;

import com.alertacore.incidentservice.domain.exception.ServicioMeteorologicoException;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.port.out.ServicioMeteorologicoPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Objects;

public class ServicioMeteorologicoResiliente
        implements ServicioMeteorologicoPort {

    private final ServicioMeteorologicoPort delegado;

    public ServicioMeteorologicoResiliente(
            ServicioMeteorologicoPort delegado
    ) {
        this.delegado = Objects.requireNonNull(
                delegado,
                "El servicio meteorologico delegado es obligatorio"
        );
    }

    @Override
    @CircuitBreaker(
            name = "openMeteo",
            fallbackMethod = "fallbackCondicionActual"
    )
    public CondicionMeteorologica consultarCondicionActual(
            double latitud,
            double longitud
    ) {
        return delegado.consultarCondicionActual(
                latitud,
                longitud
        );
    }

    private CondicionMeteorologica fallbackCondicionActual(
            double latitud,
            double longitud,
            Throwable causa
    ) {
        throw new ServicioMeteorologicoException(
                "El servicio meteorologico no se encuentra disponible",
                causa
        );
    }
}