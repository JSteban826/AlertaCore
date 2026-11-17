package com.alertacore.resourceservice.infrastructure.entrypoint.rest.advice;

import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.exception.RecursoYaReservadoException;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void deberiaRetornar404() {

        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/recursos/1"
                );

        ResponseEntity<?> response =
                handler.manejarDominioException(
                        new RecursoNoEncontradoException(
                                "1"
                        ),
                        request
                );

        assertEquals(
                404,
                response.getStatusCode().value()
        );
    }

    @Test
    void deberiaRetornar409() {

        HttpServletRequest request =
                new MockHttpServletRequest(
                        "POST",
                        "/api/v1/recursos/1/reservar"
                );

        ResponseEntity<?> response =
                handler.manejarDominioException(
                        new RecursoYaReservadoException(),
                        request
                );

        assertEquals(
                409,
                response.getStatusCode().value()
        );
    }

    @Test
    void deberiaRetornar500() {

        HttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/v1/error"
                );

        ResponseEntity<?> response =
                handler.manejarGeneral(
                        new RuntimeException(
                                "error"
                        ),
                        request
                );

        assertEquals(
                500,
                response.getStatusCode().value()
        );
    }
}