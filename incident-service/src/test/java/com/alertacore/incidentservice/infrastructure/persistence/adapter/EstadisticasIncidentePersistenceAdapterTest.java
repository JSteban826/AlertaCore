package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.infrastructure.persistence.projection.CantidadEstadoProjection;
import com.alertacore.incidentservice.infrastructure.persistence.projection.CantidadNivelRiesgoProjection;
import com.alertacore.incidentservice.infrastructure.persistence.projection.TiempoRespuestaProjection;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaEstadisticasIncidenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EstadisticasIncidentePersistenceAdapterTest {

    private JpaEstadisticasIncidenteRepository repositorioJpa;
    private EstadisticasIncidentePersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        repositorioJpa = mock(
                JpaEstadisticasIncidenteRepository.class
        );

        adapter = new EstadisticasIncidentePersistenceAdapter(
                repositorioJpa
        );
    }

    @Test
    void debeRetornarEstadisticasVaciasCuandoNoHayIncidentes() {
        when(repositorioJpa.contarTotalIncidentes())
                .thenReturn(0L);

        EstadisticasIncidentes resultado =
                adapter.obtenerEstadisticas();

        assertEquals(
                0L,
                resultado.totalIncidentes()
        );

        assertEquals(
                0L,
                resultado.incidentesValidados()
        );

        assertEquals(
                0L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                0.0,
                resultado.tiempoPromedioRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMinimoRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMaximoRespuestaMinutos()
        );

        assertEquals(
                0L,
                resultado.incidentesSinClasificar()
        );

        assertEquals(
                0,
                resultado.cantidadPorEstado().size()
        );

        assertEquals(
                0,
                resultado.cantidadPorNivelRiesgo().size()
        );

        verify(repositorioJpa)
                .contarTotalIncidentes();
    }

    @Test
    void debeCalcularEstadisticasCompletas() {
        CantidadEstadoProjection reportados =
                crearCantidadEstado(
                        "REPORTADO",
                        2L
                );

        CantidadEstadoProjection clasificados =
                crearCantidadEstado(
                        "CLASIFICADO",
                        3L
                );

        CantidadNivelRiesgoProjection sinClasificar =
                crearCantidadNivel(
                        null,
                        2L
                );

        CantidadNivelRiesgoProjection riesgoBajo =
                crearCantidadNivel(
                        20,
                        1L
                );

        CantidadNivelRiesgoProjection riesgoAlto =
                crearCantidadNivel(
                        60,
                        1L
                );

        CantidadNivelRiesgoProjection riesgoCritico =
                crearCantidadNivel(
                        95,
                        1L
                );

        TiempoRespuestaProjection tiempos =
                crearTiempos(
                        8.456,
                        2.314,
                        14.599,
                        3L
                );

        when(repositorioJpa.contarTotalIncidentes())
                .thenReturn(5L);

        when(repositorioJpa.contarAgrupadoPorEstado())
                .thenReturn(
                        List.of(
                                reportados,
                                clasificados
                        )
                );

        when(repositorioJpa.contarAgrupadoPorPuntaje())
                .thenReturn(
                        List.of(
                                sinClasificar,
                                riesgoBajo,
                                riesgoAlto,
                                riesgoCritico
                        )
                );

        when(
                repositorioJpa
                        .obtenerTiemposRespuestaInicial()
        ).thenReturn(tiempos);

        EstadisticasIncidentes resultado =
                adapter.obtenerEstadisticas();

        assertEquals(
                5L,
                resultado.totalIncidentes()
        );

        assertEquals(
                3L,
                resultado.incidentesValidados()
        );

        assertEquals(
                2L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                8.46,
                resultado.tiempoPromedioRespuestaMinutos()
        );

        assertEquals(
                2.31,
                resultado.tiempoMinimoRespuestaMinutos()
        );

        assertEquals(
                14.6,
                resultado.tiempoMaximoRespuestaMinutos()
        );

        assertEquals(
                2L,
                resultado.cantidadPorEstado()
                        .get(EstadoIncidente.REPORTADO)
        );

        assertEquals(
                3L,
                resultado.cantidadPorEstado()
                        .get(EstadoIncidente.CLASIFICADO)
        );

        assertEquals(
                2L,
                resultado.incidentesSinClasificar()
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.BAJO)
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.ALTO)
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.CRITICO)
        );

        verify(repositorioJpa)
                .contarAgrupadoPorEstado();

        verify(repositorioJpa)
                .contarAgrupadoPorPuntaje();

        verify(repositorioJpa)
                .obtenerTiemposRespuestaInicial();
    }

    @Test
    void debeAgruparPuntajesDelMismoNivel() {
        CantidadNivelRiesgoProjection puntajeOchenta =
                crearCantidadNivel(
                        80,
                        2L
                );

        CantidadNivelRiesgoProjection puntajeNoventa =
                crearCantidadNivel(
                        90,
                        3L
                );

        TiempoRespuestaProjection tiempos =
                crearTiempos(
                        0.0,
                        0.0,
                        0.0,
                        0L
                );

        when(repositorioJpa.contarTotalIncidentes())
                .thenReturn(5L);

        when(repositorioJpa.contarAgrupadoPorEstado())
                .thenReturn(List.of());

        when(repositorioJpa.contarAgrupadoPorPuntaje())
                .thenReturn(
                        List.of(
                                puntajeOchenta,
                                puntajeNoventa
                        )
                );

        when(
                repositorioJpa.obtenerTiemposRespuestaInicial()
        ).thenReturn(tiempos);

        EstadisticasIncidentes resultado =
                adapter.obtenerEstadisticas();

        assertEquals(
                5L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.CRITICO)
        );
    }

    @Test
    void debeClasificarTodosLosNivelesDeRiesgo() {
        CantidadNivelRiesgoProjection puntajeCero =
                crearCantidadNivel(0, 1L);

        CantidadNivelRiesgoProjection puntajeVeinticuatro =
                crearCantidadNivel(24, 1L);

        CantidadNivelRiesgoProjection puntajeVeinticinco =
                crearCantidadNivel(25, 1L);

        CantidadNivelRiesgoProjection puntajeCuarentaYNueve =
                crearCantidadNivel(49, 1L);

        CantidadNivelRiesgoProjection puntajeCincuenta =
                crearCantidadNivel(50, 1L);

        CantidadNivelRiesgoProjection puntajeSetentaYCuatro =
                crearCantidadNivel(74, 1L);

        CantidadNivelRiesgoProjection puntajeSetentaYCinco =
                crearCantidadNivel(75, 1L);

        CantidadNivelRiesgoProjection puntajeCien =
                crearCantidadNivel(100, 1L);

        TiempoRespuestaProjection tiempos =
                crearTiempos(
                        0.0,
                        0.0,
                        0.0,
                        0L
                );

        when(repositorioJpa.contarTotalIncidentes())
                .thenReturn(8L);

        when(repositorioJpa.contarAgrupadoPorEstado())
                .thenReturn(List.of());

        when(repositorioJpa.contarAgrupadoPorPuntaje())
                .thenReturn(
                        List.of(
                                puntajeCero,
                                puntajeVeinticuatro,
                                puntajeVeinticinco,
                                puntajeCuarentaYNueve,
                                puntajeCincuenta,
                                puntajeSetentaYCuatro,
                                puntajeSetentaYCinco,
                                puntajeCien
                        )
                );

        when(
                repositorioJpa.obtenerTiemposRespuestaInicial()
        ).thenReturn(tiempos);

        EstadisticasIncidentes resultado =
                adapter.obtenerEstadisticas();

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.BAJO)
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.MEDIO)
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.ALTO)
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.CRITICO)
        );
    }
    @Test
    void debeUsarCeroCuandoLosTiemposSonNulos() {
        TiempoRespuestaProjection tiempos =
                crearTiempos(
                        null,
                        null,
                        null,
                        null
                );

        when(repositorioJpa.contarTotalIncidentes())
                .thenReturn(4L);

        when(repositorioJpa.contarAgrupadoPorEstado())
                .thenReturn(List.of());

        when(repositorioJpa.contarAgrupadoPorPuntaje())
                .thenReturn(List.of());

        when(
                repositorioJpa
                        .obtenerTiemposRespuestaInicial()
        ).thenReturn(tiempos);

        EstadisticasIncidentes resultado =
                adapter.obtenerEstadisticas();

        assertEquals(
                0L,
                resultado.incidentesValidados()
        );

        assertEquals(
                4L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                0.0,
                resultado.tiempoPromedioRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMinimoRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRechazarRepositorioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new EstadisticasIncidentePersistenceAdapter(
                        null
                )
        );
    }

    private CantidadEstadoProjection crearCantidadEstado(
            String estado,
            Long cantidad
    ) {
        CantidadEstadoProjection projection =
                mock(CantidadEstadoProjection.class);

        when(projection.getEstado())
                .thenReturn(estado);

        when(projection.getCantidad())
                .thenReturn(cantidad);

        return projection;
    }

    private CantidadNivelRiesgoProjection crearCantidadNivel(
            Integer puntaje,
            Long cantidad
    ) {
        CantidadNivelRiesgoProjection projection =
                mock(CantidadNivelRiesgoProjection.class);

        when(projection.getPuntajeRiesgo())
                .thenReturn(puntaje);

        when(projection.getCantidad())
                .thenReturn(cantidad);

        return projection;
    }

    private TiempoRespuestaProjection crearTiempos(
            Double promedio,
            Double minimo,
            Double maximo,
            Long validados
    ) {
        TiempoRespuestaProjection projection =
                mock(TiempoRespuestaProjection.class);

        when(projection.getPromedioMinutos())
                .thenReturn(promedio);

        when(projection.getMinimoMinutos())
                .thenReturn(minimo);

        when(projection.getMaximoMinutos())
                .thenReturn(maximo);

        when(projection.getIncidentesValidados())
                .thenReturn(validados);

        return projection;
    }
}