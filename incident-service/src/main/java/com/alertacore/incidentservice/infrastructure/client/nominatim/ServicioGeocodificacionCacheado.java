package com.alertacore.incidentservice.infrastructure.client.nominatim;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import com.alertacore.incidentservice.domain.gateway.ServicioGeocodificacionPort;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

public class ServicioGeocodificacionCacheado
        implements ServicioGeocodificacionPort {

    private final ServicioGeocodificacionPort delegado;

    public ServicioGeocodificacionCacheado(
            ServicioGeocodificacionPort delegado
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