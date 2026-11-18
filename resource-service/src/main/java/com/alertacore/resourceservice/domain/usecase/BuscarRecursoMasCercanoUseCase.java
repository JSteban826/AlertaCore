package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.BuscarRecursoMasCercanoCommand;
import com.alertacore.resourceservice.domain.model.dto.RecursoCercanoDto;

import com.alertacore.resourceservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.service.CalculadorDistanciaHaversine;

import java.util.Comparator;

public class BuscarRecursoMasCercanoUseCase {

    private static final double VELOCIDAD_PROMEDIO_KM_H = 40.0;

    private final RecursoGateway recursoGateway;

    private final CalculadorDistanciaHaversine calculadorDistancia;

    public BuscarRecursoMasCercanoUseCase(
            RecursoGateway recursoGateway,
            CalculadorDistanciaHaversine calculadorDistancia
    ) {

        this.recursoGateway =
                recursoGateway;

        this.calculadorDistancia =
                calculadorDistancia;
    }

    public RecursoCercanoDto buscar(
            BuscarRecursoMasCercanoCommand command
    ) {

        return recursoGateway
                .buscarDisponiblesPorTipo(
                        command.tipoRecurso()
                )
                .stream()
                .filter(Recurso::estaDisponible)
                .map(
                        recurso ->
                                crearCandidato(
                                        recurso,
                                        command
                                )
                )
                .filter(
                        candidato ->
                                candidato.distanciaKm()
                                        <= command.distanciaMaximaKm()
                )
                .min(
                        comparadorCandidatos()
                )
                .map(
                        this::convertirADto
                )
                .orElseThrow(
                        () ->
                                new RecursoDisponibleNoEncontradoException(
                                        command.tipoRecurso(),
                                        command.distanciaMaximaKm()
                                )
                );
    }

    private CandidatoRecurso crearCandidato(
            Recurso recurso,
            BuscarRecursoMasCercanoCommand command
    ) {

        UbicacionRecurso ubicacion =
                recurso.getUbicacion();

        double distanciaKm =
                calculadorDistancia.calcularKilometros(
                        command.latitudIncidente(),
                        command.longitudIncidente(),
                        ubicacion.getLatitud(),
                        ubicacion.getLongitud()
                );

        return new CandidatoRecurso(
                recurso,
                distanciaKm
        );
    }

    private Comparator<CandidatoRecurso>
    comparadorCandidatos() {

        return Comparator
                .comparingDouble(
                        CandidatoRecurso::distanciaKm
                )
                .thenComparing(
                        candidato ->
                                candidato.recurso()
                                        .getFechaDisponibleDesde(),
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                );
    }

    private RecursoCercanoDto convertirADto(
            CandidatoRecurso candidato
    ) {

        Recurso recurso =
                candidato.recurso();

        double distanciaKm =
                candidato.distanciaKm();

        return new RecursoCercanoDto(
                recurso.getId(),
                recurso.getCodigo(),
                recurso.getTipo(),
                redondearDistancia(
                        distanciaKm
                ),
                calcularTiempoEstimado(
                        distanciaKm
                ),
                recurso.getEstado()
        );
    }

    private int calcularTiempoEstimado(
            double distanciaKm
    ) {

        double minutos =
                distanciaKm
                        / VELOCIDAD_PROMEDIO_KM_H
                        * 60;

        return Math.max(
                1,
                (int) Math.ceil(minutos)
        );
    }

    private double redondearDistancia(
            double distanciaKm
    ) {

        return Math.round(
                distanciaKm * 100.0
        ) / 100.0;
    }

    private record CandidatoRecurso(
            Recurso recurso,
            double distanciaKm
    ) {
    }
}