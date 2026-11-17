package com.alertacore.incidentservice.infrastructure.client.openmeteo;

import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.model.gateway.ServicioMeteorologicoGateway;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

public class ServicioMeteorologicoCacheado
        implements ServicioMeteorologicoGateway {

    private final ServicioMeteorologicoGateway delegado;

    public ServicioMeteorologicoCacheado(
            ServicioMeteorologicoGateway delegado
    ) {
        this.delegado = Objects.requireNonNull(
                delegado,
                "El servicio meteorologico delegado es obligatorio"
        );
    }

    @Override
    @Cacheable(
            cacheNames = "condicionesMeteorologicas",
            key = "T(java.lang.String).format("
                    + "'%.4f:%.4f', #latitud, #longitud)"
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
}