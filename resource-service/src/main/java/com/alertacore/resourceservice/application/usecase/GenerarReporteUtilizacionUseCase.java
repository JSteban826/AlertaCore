package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.dto.ReporteUtilizacionDto;
import com.alertacore.resourceservice.application.port.in.GenerarReporteUtilizacionPort;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.util.List;

public class GenerarReporteUtilizacionUseCase
        implements GenerarReporteUtilizacionPort {

    private final RepositorioRecursoPort
            repositorioRecursoPort;

    public GenerarReporteUtilizacionUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        this.repositorioRecursoPort =
                repositorioRecursoPort;
    }

    @Override
    public ReporteUtilizacionDto generar() {

        List<Recurso> recursos =
                repositorioRecursoPort.buscarTodos();

        long total =
                recursos.size();

        long disponibles =
                contar(recursos,
                        EstadoRecurso.DISPONIBLE);

        long reservados =
                contar(recursos,
                        EstadoRecurso.RESERVADO);

        long despachados =
                contar(recursos,
                        EstadoRecurso.DESPACHADO);

        long enAtencion =
                contar(recursos,
                        EstadoRecurso.EN_ATENCION);

        long fueraDeServicio =
                contar(recursos,
                        EstadoRecurso.FUERA_DE_SERVICIO);

        long operativos =
                total - fueraDeServicio;

        double porcentajeUtilizacion = 0.0;

        if (operativos > 0) {

            porcentajeUtilizacion =
                    ((double)
                            (reservados
                                    + despachados
                                    + enAtencion)
                            / operativos)
                            * 100.0;
        }

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