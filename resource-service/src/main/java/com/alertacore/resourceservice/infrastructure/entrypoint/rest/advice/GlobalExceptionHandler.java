package com.alertacore.resourceservice.infrastructure.entrypoint.rest.advice;

import com.alertacore.resourceservice.domain.exception.DominioException;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<ErrorResponse>
    manejarDominioException(
            DominioException ex,
            HttpServletRequest request
    ) {

        HttpStatus status = determinarStatus(
                ex.getCodigo()
        );

        return ResponseEntity
                .status(status)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                status.value(),
                                ex.getCodigo(),
                                ex.getMessage(),
                                request.getRequestURI(),
                                Map.of()
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    manejarValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errores =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                400,
                                "SOLICITUD_INVALIDA",
                                "Existen errores de validación",
                                request.getRequestURI(),
                                errores
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    manejarGeneral(
            Exception ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                500,
                                "ERROR_INTERNO",
                                ex.getMessage(),
                                request.getRequestURI(),
                                Map.of()
                        )
                );
    }

    private HttpStatus determinarStatus(
            String codigo
    ) {

        return switch (codigo) {

            case "RECURSO_NO_ENCONTRADO",
                 "RECURSO_DISPONIBLE_NO_ENCONTRADO"
                    -> HttpStatus.NOT_FOUND;

            case "RECURSO_NO_DISPONIBLE",
                 "TRANSICION_RECURSO_INVALIDA",
                 "RECURSO_YA_RESERVADO",
                 "RESERVA_VENCIDA",
                 "RECURSO_NO_LIBERABLE"
                    -> HttpStatus.CONFLICT;

            case "UBICACION_INVALIDA"
                    -> HttpStatus.BAD_REQUEST;

            default
                    -> HttpStatus.BAD_REQUEST;
        };
    }
}