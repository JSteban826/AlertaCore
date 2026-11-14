package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.domain.port.out.RepositorioEstadisticasIncidentePort;
import com.alertacore.incidentservice.infrastructure.persistence.projection.CantidadEstadoProjection;
import com.alertacore.incidentservice.infrastructure.persistence.projection.CantidadNivelRiesgoProjection;
import com.alertacore.incidentservice.infrastructure.persistence.projection.TiempoRespuestaProjection;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaEstadisticasIncidenteRepository;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class EstadisticasIncidentePersistenceAdapter
        implements RepositorioEstadisticasIncidentePort {

    private final JpaEstadisticasIncidenteRepository repositorioJpa;

    public EstadisticasIncidentePersistenceAdapter(
            JpaEstadisticasIncidenteRepository repositorioJpa
    ) {
        this.repositorioJpa = Objects.requireNonNull(
                repositorioJpa,
                "El repositorio de estadisticas es obligatorio"
        );
    }

    @Override
    public EstadisticasIncidentes obtenerEstadisticas() {
        long totalIncidentes =
                repositorioJpa.contarTotalIncidentes();

        if (totalIncidentes == 0) {
            return EstadisticasIncidentes.vacias();
        }

        Map<EstadoIncidente, Long> cantidadPorEstado =
                new EnumMap<>(EstadoIncidente.class);

        for (
                CantidadEstadoProjection resultado
                : repositorioJpa.contarAgrupadoPorEstado()
        ) {
            EstadoIncidente estado =
                    EstadoIncidente.valueOf(
                            resultado.getEstado()
                    );

            cantidadPorEstado.put(
                    estado,
                    resultado.getCantidad()
            );
        }

        Map<NivelRiesgo, Long> cantidadPorNivel =
                new EnumMap<>(NivelRiesgo.class);

        long incidentesSinClasificar = 0;

        for (
                CantidadNivelRiesgoProjection resultado
                : repositorioJpa.contarAgrupadoPorPuntaje()
        ) {
            Integer puntaje = resultado.getPuntajeRiesgo();

            if (puntaje == null) {
                incidentesSinClasificar =
                        resultado.getCantidad();

                continue;
            }

            NivelRiesgo nivel =
                    obtenerNivel(puntaje);

            cantidadPorNivel.merge(
                    nivel,
                    resultado.getCantidad(),
                    Long::sum
            );
        }

        TiempoRespuestaProjection tiempos =
                repositorioJpa
                        .obtenerTiemposRespuestaInicial();

        long validados = valorLong(
                tiempos.getIncidentesValidados()
        );

        long sinValidar =
                totalIncidentes - validados;

        return new EstadisticasIncidentes(
                totalIncidentes,
                validados,
                sinValidar,
                redondear(valorDouble(
                        tiempos.getPromedioMinutos()
                )),
                redondear(valorDouble(
                        tiempos.getMinimoMinutos()
                )),
                redondear(valorDouble(
                        tiempos.getMaximoMinutos()
                )),
                cantidadPorEstado,
                cantidadPorNivel,
                incidentesSinClasificar
        );
    }

    private NivelRiesgo obtenerNivel(int puntaje) {
        if (puntaje <= 24) {
            return NivelRiesgo.BAJO;
        }

        if (puntaje <= 49) {
            return NivelRiesgo.MEDIO;
        }

        if (puntaje <= 74) {
            return NivelRiesgo.ALTO;
        }

        return NivelRiesgo.CRITICO;
    }

    private double valorDouble(Double valor) {
        return valor == null ? 0 : valor;
    }

    private long valorLong(Long valor) {
        return valor == null ? 0 : valor;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}