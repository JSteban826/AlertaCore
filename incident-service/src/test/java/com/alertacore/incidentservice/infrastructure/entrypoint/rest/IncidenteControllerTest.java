package com.alertacore.incidentservice.infrastructure.entrypoint.rest;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.application.port.in.RegistrarIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception.ManejadorGlobalExcepciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncidenteControllerTest {

    private RegistrarIncidentePort registrarIncidente;
    private ClasificarIncidentePort clasificarIncidente;
    private CambiarEstadoIncidentePort cambiarEstadoIncidente;

    private MockMvc mockMvc;
    @BeforeEach
    void configurar() {
        registrarIncidente =
                mock(RegistrarIncidentePort.class);

        clasificarIncidente =
                mock(ClasificarIncidentePort.class);

        cambiarEstadoIncidente =
                mock(CambiarEstadoIncidentePort.class);

        IncidenteController controller =
                new IncidenteController(
                        registrarIncidente,
                        clasificarIncidente,
                        cambiarEstadoIncidente
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new ManejadorGlobalExcepciones()
                )
                .build();
    }


    @Test
    void debeRegistrarIncidenteYResponder201()
            throws Exception {

        UUID id = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        ResultadoIncidente resultado =
                new ResultadoIncidente(
                        id,
                        TipoIncidente.INUNDACION,
                        "Inundacion con varias personas afectadas",
                        EstadoIncidente.REPORTADO,
                        null,
                        15,
                        true,
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota",
                        fecha,
                        fecha
                );

        when(registrarIncidente.ejecutar(any()))
                .thenReturn(resultado);

        String json = """
                {
                  "tipo": "INUNDACION",
                  "descripcion": "Inundacion con varias personas afectadas",
                  "latitud": 4.6579,
                  "longitud": -74.0596,
                  "direccion": "Carrera 7 con Calle 72, Bogota",
                  "nombreReportante": "Operador de prueba",
                  "correoReportante": "operador@alertacore.com",
                  "telefonoReportante": "3001234567",
                  "cantidadPersonasAfectadas": 15,
                  "hayPersonasHeridas": true
                }
                """;

        mockMvc.perform(
                        post("/api/v1/incidentes")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/v1/incidentes/" + id
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(id.toString())
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value("REPORTADO")
                )
                .andExpect(
                        jsonPath("$.tipo")
                                .value("INUNDACION")
                );
    }

    @Test
    void debeResponder400CuandoRegistroEsInvalido()
            throws Exception {

        String json = """
                {
                  "tipo": null,
                  "descripcion": "Corta",
                  "latitud": 100,
                  "longitud": -200,
                  "direccion": "",
                  "nombreReportante": "",
                  "correoReportante": "correo-invalido",
                  "telefonoReportante": "123",
                  "cantidadPersonasAfectadas": -1,
                  "hayPersonasHeridas": false
                }
                """;

        mockMvc.perform(
                        post("/api/v1/incidentes")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.codigo")
                                .value("SOLICITUD_INVALIDA")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.erroresValidacion.tipo")
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.erroresValidacion.descripcion"
                        ).exists()
                )
                .andExpect(
                        jsonPath("$.erroresValidacion.correoReportante")
                                .exists()
                );
    }

    @Test
    void debeClasificarIncidenteYResponder200()
            throws Exception {

        UUID id = UUID.randomUUID();

        ResultadoClasificacion resultado =
                new ResultadoClasificacion(
                        id,
                        95,
                        NivelRiesgo.CRITICO,
                        10,
                        EstadoIncidente.CLASIFICADO
                );

        when(clasificarIncidente.ejecutar(any()))
                .thenReturn(resultado);

        String json = """
                {
                  "hayLluviaIntensa": true,
                  "accesoDificil": true
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/incidentes/{id}/clasificar",
                                id
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.incidenteId")
                                .value(id.toString())
                )
                .andExpect(
                        jsonPath("$.puntaje")
                                .value(95)
                )
                .andExpect(
                        jsonPath("$.nivelRiesgo")
                                .value("CRITICO")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value("CLASIFICADO")
                );
    }

    @Test
    void debeResponder404CuandoIncidenteNoExiste()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(clasificarIncidente.ejecutar(any()))
                .thenThrow(
                        new IncidenteNoEncontradoException(id)
                );

        String json = """
                {
                  "hayLluviaIntensa": true,
                  "accesoDificil": false
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/incidentes/{id}/clasificar",
                                id
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.codigo")
                                .value("INCIDENTE_NO_ENCONTRADO")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.ruta")
                                .value(
                                        "/api/v1/incidentes/"
                                                + id
                                                + "/clasificar"
                                )
                );
    }

    @Test
    void debeCambiarEstadoYResponder200()
            throws Exception {

        UUID id = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        ResultadoIncidente resultado =
                new ResultadoIncidente(
                        id,
                        TipoIncidente.INUNDACION,
                        "Inundacion con varias personas afectadas",
                        EstadoIncidente.VALIDADO,
                        null,
                        15,
                        true,
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota",
                        fecha,
                        fecha
                );

        when(cambiarEstadoIncidente.ejecutar(any()))
                .thenReturn(resultado);

        String json = """
                {
                  "nuevoEstado": "VALIDADO",
                  "motivo": "La informacion fue verificada"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/incidentes/{id}/estado",
                                id
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.estado")
                                .value("VALIDADO")
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(id.toString())
                );
    }

    @Test
    void debeResponder409AnteTransicionInvalida()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(cambiarEstadoIncidente.ejecutar(any()))
                .thenThrow(
                        new TransicionEstadoInvalidaException(
                                EstadoIncidente.REPORTADO,
                                EstadoIncidente.CERRADO
                        )
                );

        String json = """
                {
                  "nuevoEstado": "CERRADO",
                  "motivo": "Prueba de transicion invalida"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/incidentes/{id}/estado",
                                id
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.codigo")
                                .value(
                                        "TRANSICION_ESTADO_INVALIDA"
                                )
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "No es posible cambiar el incidente"
                                                + " de REPORTADO a CERRADO"
                                )
                );
    }

    @Test
    void debeResponder400CuandoMotivoEstaVacio()
            throws Exception {

        UUID id = UUID.randomUUID();

        String json = """
                {
                  "nuevoEstado": "VALIDADO",
                  "motivo": ""
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/v1/incidentes/{id}/estado",
                                id
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.codigo")
                                .value("SOLICITUD_INVALIDA")
                )
                .andExpect(
                        jsonPath("$.erroresValidacion.motivo")
                                .exists()
                );
    }
}