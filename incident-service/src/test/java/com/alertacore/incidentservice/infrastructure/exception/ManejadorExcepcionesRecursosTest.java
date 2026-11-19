package com.alertacore.incidentservice.infrastructure.exception;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.exception.IncidenteNoClasificadoException;
import com.alertacore.incidentservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.exception.ServicioRecursosException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManejadorExcepcionesRecursosTest {

    private ManejadorGlobalExcepciones manejador;
    private HttpServletRequest request;

    @BeforeEach
    void configurar() {
        manejador =
                new ManejadorGlobalExcepciones();

        request =
                mock(HttpServletRequest.class);

        when(
                request.getRequestURI()
        ).thenReturn(
                "/api/v1/incidentes/prueba/asignaciones"
        );
    }

    @Test
    void debeResponder404CuandoNoHayRecursoDisponible() {
        RecursoDisponibleNoEncontradoException excepcion =
                new RecursoDisponibleNoEncontradoException(
                        TipoRecurso.AMBULANCIA
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador
                        .manejarRecursoDisponibleNoEncontrado(
                                excepcion,
                                request
                        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                respuesta.getStatusCode()
        );

        ErrorResponse cuerpo =
                respuesta.getBody();

        assertNotNull(cuerpo);

        assertEquals(
                404,
                cuerpo.estado()
        );

        assertEquals(
                "RECURSO_DISPONIBLE_NO_ENCONTRADO",
                cuerpo.codigo()
        );

        assertEquals(
                excepcion.getMessage(),
                cuerpo.mensaje()
        );

        assertEquals(
                "/api/v1/incidentes/prueba/asignaciones",
                cuerpo.ruta()
        );
    }

    @Test
    void debeResponder409CuandoRecursoNoEstaDisponible() {
        UUID recursoId =
                UUID.randomUUID();

        RecursoNoDisponibleException excepcion =
                new RecursoNoDisponibleException(
                        recursoId
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarRecursoNoDisponible(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                respuesta.getStatusCode()
        );

        ErrorResponse cuerpo =
                respuesta.getBody();

        assertNotNull(cuerpo);

        assertEquals(
                409,
                cuerpo.estado()
        );

        assertEquals(
                "RECURSO_NO_DISPONIBLE",
                cuerpo.codigo()
        );

        assertEquals(
                excepcion.getMessage(),
                cuerpo.mensaje()
        );
    }

    @Test
    void debeResponder503CuandoResourceServiceFalla() {
        ServicioRecursosException excepcion =
                new ServicioRecursosException(
                        "Resource Service no disponible"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarServicioRecursos(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                respuesta.getStatusCode()
        );

        ErrorResponse cuerpo =
                respuesta.getBody();

        assertNotNull(cuerpo);

        assertEquals(
                503,
                cuerpo.estado()
        );

        assertEquals(
                "SERVICIO_RECURSOS_NO_DISPONIBLE",
                cuerpo.codigo()
        );

        assertEquals(
                "Resource Service no disponible",
                cuerpo.mensaje()
        );
    }

    @Test
    void debeResponder409CuandoIncidenteNoEstaClasificado() {
        UUID incidenteId =
                UUID.randomUUID();

        IncidenteNoClasificadoException excepcion =
                new IncidenteNoClasificadoException(
                        incidenteId
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarIncidenteNoClasificado(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                respuesta.getStatusCode()
        );

        ErrorResponse cuerpo =
                respuesta.getBody();

        assertNotNull(cuerpo);

        assertEquals(
                409,
                cuerpo.estado()
        );

        assertEquals(
                "INCIDENTE_NO_CLASIFICADO",
                cuerpo.codigo()
        );

        assertEquals(
                excepcion.getMessage(),
                cuerpo.mensaje()
        );
    }
}