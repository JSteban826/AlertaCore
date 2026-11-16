package com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception;

import com.alertacore.incidentservice.domain.exception.DireccionNoEncontradaException;
import com.alertacore.incidentservice.domain.exception.ServicioGeocodificacionException;
import com.alertacore.incidentservice.domain.exception.ServicioMeteorologicoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManejadorGlobalExcepcionesTest {

    private ManejadorGlobalExcepciones manejador;

    @BeforeEach
    void configurar() {
        manejador =
                new ManejadorGlobalExcepciones();
    }

    @Test
    void debeManejarArgumentoInvalidoConEstado400() {
        IllegalArgumentException excepcion =
                new IllegalArgumentException(
                        "El tamanio de pagina no puede superar 100"
                );

        MockHttpServletRequest request =
                crearRequest(
                        "/api/v1/incidentes"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarArgumentoInvalido(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                respuesta.getStatusCode()
        );

        ErrorResponse body =
                obtenerBody(respuesta);

        assertEquals(
                400,
                body.estado()
        );

        assertEquals(
                "ARGUMENTO_INVALIDO",
                body.codigo()
        );

        assertEquals(
                "El tamanio de pagina no puede superar 100",
                body.mensaje()
        );

        assertEquals(
                "/api/v1/incidentes",
                body.ruta()
        );

        assertTrue(
                body.erroresValidacion().isEmpty()
        );

        assertNotNull(
                body.timestamp()
        );
    }

    @Test
    void debeManejarDireccionNoEncontradaConEstado404() {
        String direccion =
                "Avenida Inventada XYZ 999999, Bogota, Colombia";

        DireccionNoEncontradaException excepcion =
                new DireccionNoEncontradaException(
                        direccion
                );

        MockHttpServletRequest request =
                crearRequest(
                        "/api/v1/incidentes/geocodificar"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarDireccionNoEncontrada(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                respuesta.getStatusCode()
        );

        ErrorResponse body =
                obtenerBody(respuesta);

        assertEquals(
                404,
                body.estado()
        );

        assertEquals(
                "DIRECCION_NO_ENCONTRADA",
                body.codigo()
        );

        assertTrue(
                body.mensaje().contains(direccion)
        );

        assertEquals(
                "/api/v1/incidentes/geocodificar",
                body.ruta()
        );

        assertTrue(
                body.erroresValidacion().isEmpty()
        );
    }

    @Test
    void debeManejarServicioGeocodificacionConEstado503() {
        ServicioGeocodificacionException excepcion =
                new ServicioGeocodificacionException(
                        "No fue posible consultar Nominatim"
                );

        MockHttpServletRequest request =
                crearRequest(
                        "/api/v1/incidentes/geocodificar"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarServicioGeocodificacion(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                respuesta.getStatusCode()
        );

        ErrorResponse body =
                obtenerBody(respuesta);

        assertEquals(
                503,
                body.estado()
        );

        assertEquals(
                "SERVICIO_GEOCODIFICACION_NO_DISPONIBLE",
                body.codigo()
        );

        assertEquals(
                "No fue posible consultar Nominatim",
                body.mensaje()
        );

        assertEquals(
                "/api/v1/incidentes/geocodificar",
                body.ruta()
        );
    }

    @Test
    void debeManejarServicioMeteorologicoConEstado503() {
        ServicioMeteorologicoException excepcion =
                new ServicioMeteorologicoException(
                        "El servicio meteorologico no se encuentra disponible"
                );

        MockHttpServletRequest request =
                crearRequest(
                        "/api/v1/incidentes/"
                                + "11111111-1111-1111-1111-111111111111"
                                + "/riesgo-meteorologico"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarServicioMeteorologico(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                respuesta.getStatusCode()
        );

        ErrorResponse body =
                obtenerBody(respuesta);

        assertEquals(
                503,
                body.estado()
        );

        assertEquals(
                "SERVICIO_METEOROLOGICO_NO_DISPONIBLE",
                body.codigo()
        );

        assertEquals(
                "El servicio meteorologico no se encuentra disponible",
                body.mensaje()
        );

        assertEquals(
                "/api/v1/incidentes/"
                        + "11111111-1111-1111-1111-111111111111"
                        + "/riesgo-meteorologico",
                body.ruta()
        );
    }

    @Test
    void debeManejarErrorInesperadoConEstado500() {
        RuntimeException excepcion =
                new RuntimeException(
                        "Error sensible de base de datos"
                );

        MockHttpServletRequest request =
                crearRequest(
                        "/api/v1/incidentes"
                );

        ResponseEntity<ErrorResponse> respuesta =
                manejador.manejarErrorInesperado(
                        excepcion,
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                respuesta.getStatusCode()
        );

        ErrorResponse body =
                obtenerBody(respuesta);

        assertEquals(
                500,
                body.estado()
        );

        assertEquals(
                "ERROR_INTERNO",
                body.codigo()
        );

        assertEquals(
                "Ocurrio un error interno inesperado",
                body.mensaje()
        );

        assertEquals(
                "/api/v1/incidentes",
                body.ruta()
        );

        assertTrue(
                body.erroresValidacion().isEmpty()
        );

        /*
         * También confirmamos que la respuesta no exponga
         * el mensaje técnico original.
         */
        assertTrue(
                !body.mensaje().contains(
                        "base de datos"
                )
        );
    }

    private MockHttpServletRequest crearRequest(
            String ruta
    ) {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(ruta);

        return request;
    }

    private ErrorResponse obtenerBody(
            ResponseEntity<ErrorResponse> respuesta
    ) {
        ErrorResponse body =
                respuesta.getBody();

        assertNotNull(body);

        return body;
    }
}