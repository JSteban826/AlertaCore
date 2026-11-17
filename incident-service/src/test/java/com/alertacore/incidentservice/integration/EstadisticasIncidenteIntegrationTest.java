package com.alertacore.incidentservice.integration;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.model.gateway.EstadisticasIncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
@Transactional
class EstadisticasIncidenteIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer(
                    "postgres:17-alpine"
            )
                    .withDatabaseName(
                            "alertacore_test"
                    )
                    .withUsername(
                            "alertacore_test"
                    )
                    .withPassword(
                            "alertacore_test"
                    );

    @Autowired
    private IncidenteGateway
            repositorioIncidente;

    @Autowired
    private HistorialEstadoGateway
            repositorioHistorial;

    @Autowired
    private EstadisticasIncidenteGateway
            repositorioEstadisticas;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debeRetornarEstadisticasVacias() {
        EstadisticasIncidentes resultado =
                repositorioEstadisticas
                        .obtenerEstadisticas();

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

        assertTrue(
                resultado.cantidadPorEstado().isEmpty()
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo().isEmpty()
        );
    }

    @Test
    void debeCalcularEstadisticasRealesEnPostgreSql() {
        /*
         * Incidente 1:
         * permanece REPORTADO y sin clasificar.
         */
        Incidente reportado =
                repositorioIncidente.guardar(
                        crearIncidente(
                                TipoIncidente.FALLA_ELECTRICA,
                                "Falla electrica pendiente de validacion"
                        )
                );

        /*
         * Incidente 2:
         * llega a VALIDADO, con tiempo de respuesta
         * inicial de 5 minutos.
         */
        Incidente validado =
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incendio revisado por el operador"
                );

        LocalDateTime reporteValidado =
                validado.getFechaReporte();

        validado.validar();

        validado =
                repositorioIncidente.guardar(
                        validado
                );

        guardarValidacion(
                validado.getId(),
                reporteValidado.plusMinutes(5),
                "Informacion del incendio verificada"
        );

        /*
         * Incidente 3:
         * llega a CLASIFICADO con nivel ALTO.
         * Tiempo de respuesta inicial: 10 minutos.
         */
        Incidente riesgoAlto =
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion clasificada con riesgo alto"
                );

        LocalDateTime reporteRiesgoAlto =
                riesgoAlto.getFechaReporte();

        riesgoAlto.validar();

        riesgoAlto.clasificar(
                new PuntajeRiesgo(60)
        );

        riesgoAlto =
                repositorioIncidente.guardar(
                        riesgoAlto
                );

        guardarValidacion(
                riesgoAlto.getId(),
                reporteRiesgoAlto.plusMinutes(10),
                "Informacion de la inundacion verificada"
        );

        /*
         * Incidente 4:
         * llega a CLASIFICADO con nivel CRITICO.
         * Tiempo de respuesta inicial: 15 minutos.
         */
        Incidente riesgoCritico =
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incendio clasificado con riesgo critico"
                );

        LocalDateTime reporteRiesgoCritico =
                riesgoCritico.getFechaReporte();

        riesgoCritico.validar();

        riesgoCritico.clasificar(
                new PuntajeRiesgo(95)
        );

        riesgoCritico =
                repositorioIncidente.guardar(
                        riesgoCritico
                );

        guardarValidacion(
                riesgoCritico.getId(),
                reporteRiesgoCritico.plusMinutes(15),
                "Informacion del incendio critico verificada"
        );

        /*
         * Fuerza a Hibernate a enviar todos los INSERT y UPDATE
         * antes de ejecutar las consultas SQL nativas.
         */
        entityManager.flush();
        entityManager.clear();

        EstadisticasIncidentes resultado =
                repositorioEstadisticas
                        .obtenerEstadisticas();

        assertEquals(
                4L,
                resultado.totalIncidentes()
        );

        assertEquals(
                3L,
                resultado.incidentesValidados()
        );

        assertEquals(
                1L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                1L,
                resultado.cantidadPorEstado()
                        .get(EstadoIncidente.REPORTADO)
        );

        assertEquals(
                1L,
                resultado.cantidadPorEstado()
                        .get(EstadoIncidente.VALIDADO)
        );

        assertEquals(
                2L,
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
                        .get(NivelRiesgo.ALTO)
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get(NivelRiesgo.CRITICO)
        );

        /*
         * Tiempos utilizados:
         *
         * 5 minutos
         * 10 minutos
         * 15 minutos
         *
         * Promedio = 10 minutos.
         */
        assertEquals(
                10.0,
                resultado.tiempoPromedioRespuestaMinutos(),
                0.05
        );

        assertEquals(
                5.0,
                resultado.tiempoMinimoRespuestaMinutos(),
                0.05
        );

        assertEquals(
                15.0,
                resultado.tiempoMaximoRespuestaMinutos(),
                0.05
        );

        /*
         * Evita advertencias por la variable local y confirma
         * que el incidente REPORTADO se guardó realmente.
         */
        assertEquals(
                EstadoIncidente.REPORTADO,
                reportado.getEstado()
        );
    }

    @Test
    void debeUsarLaPrimeraValidacionParaElTiempoDeRespuesta() {
        Incidente incidente =
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion con dos registros de validacion"
                );

        LocalDateTime fechaReporte =
                incidente.getFechaReporte();

        incidente.validar();

        incidente =
                repositorioIncidente.guardar(
                        incidente
                );

        /*
         * Primera validación a los 4 minutos.
         */
        guardarValidacion(
                incidente.getId(),
                fechaReporte.plusMinutes(4),
                "Primera validacion"
        );

        /*
         * Segundo registro hacia VALIDADO a los 20 minutos.
         * La consulta debe utilizar MIN(fecha_cambio),
         * por tanto debe conservar 4 minutos.
         */
        guardarValidacion(
                incidente.getId(),
                fechaReporte.plusMinutes(20),
                "Validacion repetida posterior"
        );

        entityManager.flush();
        entityManager.clear();

        EstadisticasIncidentes resultado =
                repositorioEstadisticas
                        .obtenerEstadisticas();

        assertEquals(
                1L,
                resultado.totalIncidentes()
        );

        assertEquals(
                1L,
                resultado.incidentesValidados()
        );

        assertEquals(
                4.0,
                resultado.tiempoPromedioRespuestaMinutos(),
                0.05
        );

        assertEquals(
                4.0,
                resultado.tiempoMinimoRespuestaMinutos(),
                0.05
        );

        assertEquals(
                4.0,
                resultado.tiempoMaximoRespuestaMinutos(),
                0.05
        );
    }

    private Incidente crearIncidente(
            TipoIncidente tipo,
            String descripcion
    ) {
        return Incidente.reportar(
                tipo,
                descripcion,
                new Ubicacion(
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota"
                ),
                new InformacionContacto(
                        "Operador de integracion",
                        "integracion@alertacore.com",
                        "3001234567"
                ),
                10,
                true
        );
    }

    private void guardarValidacion(
            UUID incidenteId,
            LocalDateTime fechaCambio,
            String motivo
    ) {
        RegistroHistorialEstado registro =
                new RegistroHistorialEstado(
                        UUID.randomUUID(),
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        motivo,
                        "OPERADOR_INTEGRACION",
                        fechaCambio
                );

        repositorioHistorial.guardar(registro);
    }
}