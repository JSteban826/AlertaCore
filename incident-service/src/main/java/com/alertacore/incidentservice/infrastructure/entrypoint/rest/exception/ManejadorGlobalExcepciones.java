package com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception;

import com.alertacore.incidentservice.domain.exception.IncidenteInvalidoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(IncidenteNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarIncidenteNoEncontrado(
            IncidenteNoEncontradoException excepcion,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.crear(
                HttpStatus.NOT_FOUND.value(),
                "INCIDENTE_NO_ENCONTRADO",
                excepcion.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> manejarTransicionInvalida(
            TransicionEstadoInvalidaException excepcion,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.crear(
                HttpStatus.CONFLICT.value(),
                "TRANSICION_ESTADO_INVALIDA",
                excepcion.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(IncidenteInvalidoException.class)
    public ResponseEntity<ErrorResponse> manejarIncidenteInvalido(
            IncidenteInvalidoException excepcion,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.crear(
                HttpStatus.BAD_REQUEST.value(),
                "INCIDENTE_INVALIDO",
                excepcion.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoInvalido(
            IllegalArgumentException excepcion,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.crear(
                HttpStatus.BAD_REQUEST.value(),
                "ARGUMENTO_INVALIDO",
                excepcion.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(
            MethodArgumentNotValidException excepcion,
            HttpServletRequest request
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        excepcion.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        ErrorResponse respuesta =
                ErrorResponse.crearConValidaciones(
                        HttpStatus.BAD_REQUEST.value(),
                        "SOLICITUD_INVALIDA",
                        "La solicitud contiene datos invalidos",
                        request.getRequestURI(),
                        errores
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInesperado(
            Exception excepcion,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.crear(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "ERROR_INTERNO",
                "Ocurrio un error interno inesperado",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}