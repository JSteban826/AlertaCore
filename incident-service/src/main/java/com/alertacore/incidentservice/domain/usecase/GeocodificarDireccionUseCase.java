package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.model.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.application.port.in.GeocodificarDireccionPort;
import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import com.alertacore.incidentservice.domain.model.gateway.ServicioGeocodificacionGateway;

import java.util.Objects;

public final class GeocodificarDireccionUseCase
        implements GeocodificarDireccionPort {

    private final ServicioGeocodificacionGateway
            servicioGeocodificacion;

    public GeocodificarDireccionUseCase(
            ServicioGeocodificacionGateway
                    servicioGeocodificacion
    ) {
        this.servicioGeocodificacion =
                Objects.requireNonNull(
                        servicioGeocodificacion,
                        "El servicio de geocodificacion es obligatorio"
                );
    }

    @Override
    public GeocodificacionResultado ejecutar(
            GeocodificarDireccionCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de geocodificacion es obligatorio"
        );

        ResultadoGeocodificacion resultado =
                servicioGeocodificacion.geocodificar(
                        comando.direccion()
                );

        return GeocodificacionResultado.desde(
                resultado
        );
    }
}