package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.port.out.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.domain.service.CalculadoraRiesgo;
import com.alertacore.incidentservice.domain.service.ContextoRiesgo;

import java.util.Objects;

public final class ClasificarIncidenteUseCase
        implements ClasificarIncidentePort {

    private static final String MOTIVO_CLASIFICACION =
            "Clasificacion automatica de riesgo";

    private static final String RESPONSABLE_CLASIFICACION =
            "SISTEMA_CLASIFICACION";

    private final RepositorioIncidentePort repositorioIncidente;
    private final RepositorioHistorialEstadoPort repositorioHistorial;
    private final CalculadoraRiesgo calculadoraRiesgo;

    public ClasificarIncidenteUseCase(
            RepositorioIncidentePort repositorioIncidente,
            RepositorioHistorialEstadoPort repositorioHistorial,
            CalculadoraRiesgo calculadoraRiesgo
    ) {
        this.repositorioIncidente = Objects.requireNonNull(
                repositorioIncidente,
                "El repositorio de incidentes es obligatorio"
        );

        this.repositorioHistorial = Objects.requireNonNull(
                repositorioHistorial,
                "El repositorio de historial es obligatorio"
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

        Incidente incidente = repositorioIncidente
                .buscarPorId(comando.incidenteId())
                .orElseThrow(
                        () -> new IncidenteNoEncontradoException(
                                comando.incidenteId()
                        )
                );

        EstadoIncidente estadoAnterior =
                incidente.getEstado();

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
                repositorioIncidente.guardar(incidente);

        RegistroHistorialEstado registro =
                RegistroHistorialEstado.crear(
                        incidenteGuardado.getId(),
                        estadoAnterior,
                        incidenteGuardado.getEstado(),
                        MOTIVO_CLASIFICACION,
                        RESPONSABLE_CLASIFICACION
                );

        repositorioHistorial.guardar(registro);

        return ResultadoClasificacion.desde(
                incidenteGuardado
        );
    }
}