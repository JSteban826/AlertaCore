package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.dto.ReporteUtilizacionDto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.util.List;

public class GenerarReporteUtilizacionUseCase {

    private final RecursoGateway recursoGateway;

    public GenerarReporteUtilizacionUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                recursoGateway;
    }

    public ReporteUtilizacionDto generar() {

        List<Recurso> recursos =
                recursoGateway.buscarTodos();

        long total =
                recursos.size();

        long disponibles =
                contar(
                        recursos,
                        EstadoRecurso.DISPONIBLE
                );

        long reservados =
                contar(
                        recursos,
                        EstadoRecurso.RESERVADO
                );

        long despachados =
                contar(
                        recursos,
                        EstadoRecurso.DESPACHADO
                );

        long enAtencion =
                contar(
                        recursos,
                        EstadoRecurso.EN_ATENCION
                );

        long fueraDeServicio =
                contar(
                        recursos,
                        EstadoRecurso.FUERA_DE_SERVICIO
                );

        long operativos =
                total - fueraDeServicio;

        double porcentajeUtilizacion =
                operativos > 0
                        ? (
                        (double)
                                (
                                        reservados
                                                + despachados
                                                + enAtencion
                                )
                                / operativos
                ) * 100
                        : 0;

        return new ReporteUtilizacionDto(
                total,
                disponibles,
                reservados,
                despachados,
                enAtencion,
                fueraDeServicio,
                porcentajeUtilizacion
        );
    }

    private long contar(
            List<Recurso> recursos,
            EstadoRecurso estado
    ) {

        return recursos.stream()
                .filter(
                        recurso ->
                                recurso.getEstado()
                                        == estado
                )
                .count();
    }
}