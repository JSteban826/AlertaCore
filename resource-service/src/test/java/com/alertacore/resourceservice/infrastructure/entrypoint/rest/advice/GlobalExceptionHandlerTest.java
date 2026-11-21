package com.alertacore.resourceservice.infrastructure.entrypoint.rest.advice;

import com.alertacore.resourceservice.domain.exception.DominioException;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void deberiaRetornar404ParaRecursoNoEncontrado() {
        verificarExcepcionDominio(
                "RECURSO_NO_ENCONTRADO",
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void deberiaRetornar404CuandoNoHayRecursoDisponible() {
        verificarExcepcionDominio(
                "RECURSO_DISPONIBLE_NO_ENCONTRADO",
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void deberiaRetornar409ParaTodosLosConflictos() {
        List<String> codigos = List.of(
                "RECURSO_NO_DISPONIBLE",
                "TRANSICION_RECURSO_INVALIDA",
                "RECURSO_YA_RESERVADO",
                "RESERVA_VENCIDA",
                "RECURSO_NO_LIBERABLE"
        );

        for (String codigo : codigos) {
            verificarExcepcionDominio(
                    codigo,
                    HttpStatus.CONFLICT
            );
        }
    }

    @Test
    void deberiaRetornar400ParaUbicacionInvalida() {
        verificarExcepcionDominio(
                "UBICACION_INVALIDA",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void deberiaRetornar400ParaCodigoNoReconocido() {
        verificarExcepcionDominio(
                "CODIGO_DESCONOCIDO",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    void deberiaConstruirRespuestaDeDominioConCuerpo() {
        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/recursos/123"
                );

        DominioException excepcion =
                new DominioExceptionPrueba(
                        "RECURSO_NO_ENCONTRADO",
                        "El recurso solicitado no existe"
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarDominioException(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                )
        );
    }

    @Test
    void deberiaRetornar400ConErroresDeValidacion() {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/v1/recursos"
                );

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "registrarRecursoRequest"
                );

        bindingResult.addError(
                new FieldError(
                        "registrarRecursoRequest",
                        "codigo",
                        "",
                        false,
                        null,
                        null,
                        "El codigo es obligatorio"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "registrarRecursoRequest",
                        "nombre",
                        "",
                        false,
                        null,
                        null,
                        "El nombre es obligatorio"
                )
        );

        MethodArgumentNotValidException excepcion =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarValidacion(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                ),
                () -> assertEquals(
                        2,
                        excepcion.getBindingResult()
                                .getFieldErrors()
                                .size()
                )
        );
    }

    @Test
    void deberiaProcesarDosErroresDelMismoCampo() {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/v1/recursos"
                );

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "request"
                );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "codigo",
                        "El codigo es obligatorio"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "codigo",
                        "El codigo tiene formato invalido"
                )
        );

        MethodArgumentNotValidException excepcion =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarValidacion(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                ),
                () -> assertEquals(
                        2,
                        excepcion.getBindingResult()
                                .getFieldErrors()
                                .size()
                )
        );
    }

    @Test
    void deberiaRetornar400AunqueNoExistanErroresDeCampo() {
        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/v1/recursos"
                );

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "request"
                );

        MethodArgumentNotValidException excepcion =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarValidacion(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                ),
                () -> assertEquals(
                        0,
                        excepcion.getBindingResult()
                                .getFieldErrors()
                                .size()
                )
        );
    }

    @Test
    void deberiaRetornar500ParaExcepcionGeneral() {
        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/error"
                );

        RuntimeException excepcion =
                new RuntimeException(
                        "Error interno de prueba"
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarGeneral(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                )
        );
    }

    @Test
    void deberiaRetornar500AunqueElMensajeSeaNulo() {
        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/error-sin-mensaje"
                );

        Exception excepcion =
                new Exception();

        ResponseEntity<ErrorResponse> response =
                handler.manejarGeneral(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                )
        );
    }

    private void verificarExcepcionDominio(
            String codigo,
            HttpStatus statusEsperado
    ) {
        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/recursos/prueba"
                );

        DominioException excepcion =
                new DominioExceptionPrueba(
                        codigo,
                        "Mensaje de prueba"
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarDominioException(
                        excepcion,
                        request
                );

        assertAll(
                () -> assertEquals(
                        statusEsperado,
                        response.getStatusCode()
                ),
                () -> assertNotNull(
                        response.getBody()
                )
        );
    }

    private static final class DominioExceptionPrueba
            extends DominioException {

        private DominioExceptionPrueba(
                String codigo,
                String mensaje
        ) {
            super(codigo, mensaje);
        }
    }
}