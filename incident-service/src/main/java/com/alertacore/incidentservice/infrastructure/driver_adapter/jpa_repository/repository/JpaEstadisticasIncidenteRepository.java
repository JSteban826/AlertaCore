package com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository;

import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.projection.CantidadEstadoProjection;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.projection.CantidadNivelRiesgoProjection;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.projection.TiempoRespuestaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaEstadisticasIncidenteRepository
        extends JpaRepository<IncidenteEntity, UUID> {

    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM incidents.incidentes
                    """,
            nativeQuery = true
    )
    long contarTotalIncidentes();

    @Query(
            value = """
                    SELECT
                        estado AS estado,
                        COUNT(*) AS cantidad
                    FROM incidents.incidentes
                    GROUP BY estado
                    ORDER BY estado
                    """,
            nativeQuery = true
    )
    List<CantidadEstadoProjection>
    contarAgrupadoPorEstado();

    @Query(
            value = """
                    SELECT
                        puntaje_riesgo AS "puntajeRiesgo",
                        COUNT(*) AS cantidad
                    FROM incidents.incidentes
                    GROUP BY puntaje_riesgo
                    ORDER BY puntaje_riesgo
                    """,
            nativeQuery = true
    )
    List<CantidadNivelRiesgoProjection>
    contarAgrupadoPorPuntaje();

    @Query(
            value = """
                    WITH primeras_validaciones AS (
                        SELECT
                            i.id AS incidente_id,
                            i.fecha_reporte,
                            MIN(h.fecha_cambio) AS fecha_validacion
                        FROM incidents.incidentes i
                        INNER JOIN incidents.historial_estados_incidente h
                            ON h.incidente_id = i.id
                        WHERE h.nuevo_estado = 'VALIDADO'
                        GROUP BY
                            i.id,
                            i.fecha_reporte
                    )
                    SELECT
                        COALESCE(
                            AVG(
                                EXTRACT(
                                    EPOCH FROM (
                                        fecha_validacion
                                        - fecha_reporte
                                    )
                                ) / 60.0
                            ),
                            0
                        ) AS "promedioMinutos",

                        COALESCE(
                            MIN(
                                EXTRACT(
                                    EPOCH FROM (
                                        fecha_validacion
                                        - fecha_reporte
                                    )
                                ) / 60.0
                            ),
                            0
                        ) AS "minimoMinutos",

                        COALESCE(
                            MAX(
                                EXTRACT(
                                    EPOCH FROM (
                                        fecha_validacion
                                        - fecha_reporte
                                    )
                                ) / 60.0
                            ),
                            0
                        ) AS "maximoMinutos",

                        COUNT(*) AS "incidentesValidados"
                    FROM primeras_validaciones
                    """,
            nativeQuery = true
    )
    TiempoRespuestaProjection
    obtenerTiemposRespuestaInicial();
}