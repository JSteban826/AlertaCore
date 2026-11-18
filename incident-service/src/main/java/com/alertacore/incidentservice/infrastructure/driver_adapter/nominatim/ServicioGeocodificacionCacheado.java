package com.alertacore.incidentservice.infrastructure.driver_adapter.nominatim;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import com.alertacore.incidentservice.domain.model.gateway.ServicioGeocodificacionGateway;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

public class ServicioGeocodificacionCacheado
        implements ServicioGeocodificacionGateway {

    private final ServicioGeocodificacionGateway delegado;

    public ServicioGeocodificacionCacheado(
            ServicioGeocodificacionGateway delegado
    ) {
        this.delegado = Objects.requireNonNull(
                delegado,
                "El servicio de geocodificacion delegado es obligatorio"
        );
    }

    @Override
    @Cacheable(
            cacheNames = "geocodificaciones",
            key = "#direccion.trim().toLowerCase()"
    )
    public ResultadoGeocodificacion geocodificar(
            String direccion
    ) {
        return delegado.geocodificar(direccion);
    }
}