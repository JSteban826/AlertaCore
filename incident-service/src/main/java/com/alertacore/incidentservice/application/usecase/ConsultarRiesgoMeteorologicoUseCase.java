package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.dto.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.application.port.in.ConsultarRiesgoMeteorologicoPort;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.domain.port.out.ServicioMeteorologicoPort;

import java.util.Objects;
import java.util.UUID;

public final class ConsultarRiesgoMeteorologicoUseCase
        implements ConsultarRiesgoMeteorologicoPort {

    private final RepositorioIncidentePort repositorioIncidente;
    private final ServicioMeteorologicoPort servicioMeteorologico;

    public ConsultarRiesgoMeteorologicoUseCase(
            RepositorioIncidentePort repositorioIncidente,
            ServicioMeteorologicoPort servicioMeteorologico
    ) {
        this.repositorioIncidente = Objects.requireNonNull(
                repositorioIncidente,
                "El repositorio de incidentes es obligatorio"
        );

        this.servicioMeteorologico = Objects.requireNonNull(
                servicioMeteorologico,
                "El servicio meteorologico es obligatorio"
        );
    }

    @Override
    public RiesgoMeteorologicoResultado ejecutar(
            UUID incidenteId
    ) {
        Objects.requireNonNull(
                incidenteId,
                "El identificador del incidente es obligatorio"
        );

        Incidente incidente = repositorioIncidente
                .buscarPorId(incidenteId)
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                incidenteId
                        )
                );

        CondicionMeteorologica condicion =
                servicioMeteorologico
                        .consultarCondicionActual(
                                incidente
                                        .getUbicacion()
                                        .getLatitud(),
                                incidente
                                        .getUbicacion()
                                        .getLongitud()
                        );

        return RiesgoMeteorologicoResultado.desde(
                incidenteId,
                condicion
        );
    }
}