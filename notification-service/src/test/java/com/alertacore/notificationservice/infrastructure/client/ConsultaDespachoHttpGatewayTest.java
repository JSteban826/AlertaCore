package com.alertacore.notificationservice.infrastructure.client;

import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway.IncidenteDetalle;
import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway.RecursoDetalle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;

import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ConsultaDespachoHttpGatewayTest {

    private RestClient incidentClient;
    private RestClient resourceClient;

    private ConsultaDespachoHttpGateway gateway;

    @BeforeEach
    void prepararGateway() {
        incidentClient = mock(
                RestClient.class,
                RETURNS_DEEP_STUBS
        );

        resourceClient = mock(
                RestClient.class,
                RETURNS_DEEP_STUBS
        );

        RestClient.Builder builder = mock(
                RestClient.Builder.class,
                RETURNS_SELF
        );

        try (MockedStatic<RestClient> restClientStatic =
                     mockStatic(RestClient.class)) {

            restClientStatic.when(RestClient::builder)
                    .thenReturn(builder);

            when(builder.build())
                    .thenReturn(incidentClient, resourceClient);

            gateway = new ConsultaDespachoHttpGateway(
                    "http://incidentes-prueba",
                    "http://recursos-prueba"
            );
        }
    }

    @Test
    void debeDevolverElDetalleDelIncidente() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();

        IncidenteDetalle esperado = new IncidenteDetalle(
                incidenteId,
                "INCENDIO",
                "Incendio en una vivienda",
                "REPORTADO",
                "ALTO",
                3,
                true,
                4.6097,
                -74.0817,
                "Calle 10 # 20-30",
                "2026-11-21T12:00:00",
                "2026-11-21T12:05:00"
        );

        when(incidentClient.get()
                .uri("/api/v1/incidentes/{id}", incidenteId)
                .retrieve()
                .body(IncidenteDetalle.class))
                .thenReturn(esperado);

        // Ejecutar
        IncidenteDetalle resultado =
                gateway.consultarIncidente(incidenteId);

        // Comprobar
        assertSame(esperado, resultado);
    }

    @Test
    void debeLanzarExcepcionCuandoElIncidenteTieneRespuestaVacia() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();

        when(incidentClient.get()
                .uri("/api/v1/incidentes/{id}", incidenteId)
                .retrieve()
                .body(IncidenteDetalle.class))
                .thenReturn(null);

        // Ejecutar
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> gateway.consultarIncidente(incidenteId)
        );

        // Comprobar
        assertEquals(
                "Incident Service devolvió una respuesta vacía",
                excepcion.getMessage()
        );
    }

    @Test
    void debeDevolverElDetalleDelRecurso() {
        // Preparar
        UUID recursoId = UUID.randomUUID();

        RecursoDetalle esperado = new RecursoDetalle(
                recursoId,
                "BOM-001",
                "Unidad de bomberos 1",
                "BOMBEROS",
                "DESPACHADO",
                4.6100,
                -74.0800
        );

        when(resourceClient.get()
                .uri("/api/v1/recursos/{id}", recursoId)
                .retrieve()
                .body(RecursoDetalle.class))
                .thenReturn(esperado);

        // Ejecutar
        RecursoDetalle resultado =
                gateway.consultarRecurso(recursoId);

        // Comprobar
        assertSame(esperado, resultado);
    }

    @Test
    void debeLanzarExcepcionCuandoElRecursoTieneRespuestaVacia() {
        // Preparar
        UUID recursoId = UUID.randomUUID();

        when(resourceClient.get()
                .uri("/api/v1/recursos/{id}", recursoId)
                .retrieve()
                .body(RecursoDetalle.class))
                .thenReturn(null);

        // Ejecutar
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> gateway.consultarRecurso(recursoId)
        );

        // Comprobar
        assertEquals(
                "Resource Service devolvió una respuesta vacía",
                excepcion.getMessage()
        );
    }
}