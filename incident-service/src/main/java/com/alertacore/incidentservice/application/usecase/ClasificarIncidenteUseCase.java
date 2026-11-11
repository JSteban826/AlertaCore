package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.domain.service.CalculadoraRiesgo;
import com.alertacore.incidentservice.domain.service.ContextoRiesgo;

import java.util.Objects;

public final class ClasificarIncidenteUseCase
        implements ClasificarIncidentePort {

    private final RepositorioIncidentePort repositorio;
    private final CalculadoraRiesgo calculadoraRiesgo;

    public ClasificarIncidenteUseCase(
            RepositorioIncidentePort repositorio,
            CalculadoraRiesgo calculadoraRiesgo
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de incidentes es obligatorio"
        );

        this.calculadoraRiesgo = Objects.requireNonNull(
                calculadoraRiesgo,
                "La calculadora de riesgo es obligatoria"
        );
    }

    @Override
    public ResultadoClasificacion ejecutar(
            ClasificarIncidenteCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de clasificacion es obligatorio"
        );

        Incidente incidente = repositorio
                .buscarPorId(comando.incidenteId())
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                comando.incidenteId()
                        )
                );

        ContextoRiesgo contexto = new ContextoRiesgo(
                incidente.getTipo(),
                incidente.getCantidadPersonasAfectadas(),
                incidente.hayPersonasHeridas(),
                comando.hayLluviaIntensa(),
                comando.accesoDificil()
        );

        PuntajeRiesgo puntaje =
                calculadoraRiesgo.calcular(contexto);

        incidente.clasificar(puntaje);

        Incidente incidenteGuardado =
                repositorio.guardar(incidente);

        return ResultadoClasificacion.desde(
                incidenteGuardado
        );
    }
}