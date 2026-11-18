package com.alertacore.incidentservice.infrastructure.exception;

import com.alertacore.incidentservice.domain.exception.DireccionNoEncontradaException;
import com.alertacore.incidentservice.domain.exception.IncidenteInvalidoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.exception.ServicioGeocodificacionException;
import com.alertacore.incidentservice.domain.exception.ServicioMeteorologicoException;
import com.alertacore.incidentservice.domain.exception.ServicioRecursosException;
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
    public ResponseEntity<ErrorResponse>
    manejarIncidenteNoEncontrado(
            IncidenteNoEncontradoException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
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
    public ResponseEntity<ErrorResponse>
    manejarTransicionInvalida(
            TransicionEstadoInvalidaException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.CONFLICT.value(),
                        "TRANSICION_ESTADO_INVALIDA",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ServicioMeteorologicoException.class)
    public ResponseEntity<ErrorResponse>
    manejarServicioMeteorologico(
            ServicioMeteorologicoException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "SERVICIO_METEOROLOGICO_NO_DISPONIBLE",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    @ExceptionHandler(IncidenteInvalidoException.class)
    public ResponseEntity<ErrorResponse>
    manejarIncidenteInvalido(
            IncidenteInvalidoException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
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
    public ResponseEntity<ErrorResponse>
    manejarArgumentoInvalido(
            IllegalArgumentException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
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
    public ResponseEntity<ErrorResponse>
    manejarValidaciones(
            MethodArgumentNotValidException excepcion,
            HttpServletRequest request
    ) {

        Map<String, String> errores =
                new LinkedHashMap<>();

        excepcion.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error ->
                                errores.putIfAbsent(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                );

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

    @ExceptionHandler(DireccionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse>
    manejarDireccionNoEncontrada(
            DireccionNoEncontradaException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.NOT_FOUND.value(),
                        "DIRECCION_NO_ENCONTRADA",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ServicioGeocodificacionException.class)
    public ResponseEntity<ErrorResponse>
    manejarServicioGeocodificacion(
            ServicioGeocodificacionException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "SERVICIO_GEOCODIFICACION_NO_DISPONIBLE",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    @ExceptionHandler(
            RecursoDisponibleNoEncontradoException.class
    )
    public ResponseEntity<ErrorResponse>
    manejarRecursoDisponibleNoEncontrado(
            RecursoDisponibleNoEncontradoException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.NOT_FOUND.value(),
                        "RECURSO_DISPONIBLE_NO_ENCONTRADO",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(RecursoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse>
    manejarRecursoNoDisponible(
            RecursoNoDisponibleException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.CONFLICT.value(),
                        "RECURSO_NO_DISPONIBLE",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ServicioRecursosException.class)
    public ResponseEntity<ErrorResponse>
    manejarServicioRecursos(
            ServicioRecursosException excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "SERVICIO_RECURSOS_NO_DISPONIBLE",
                        excepcion.getMessage(),
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    /*
     * Este manejador debe permanecer al final porque captura
     * cualquier excepción que no tenga un tratamiento específico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    manejarErrorInesperado(
            Exception excepcion,
            HttpServletRequest request
    ) {

        ErrorResponse error =
                ErrorResponse.crear(
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