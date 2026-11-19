package com.alertacore.incidentservice.infrastructure.entry_points.rest;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.model.AsignacionRecursoResultado;

import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.AsignarRecursoRequest;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.domain.model.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.domain.usecase.IncidenteUseCase;
import com.alertacore.incidentservice.infrastructure.exception.ErrorResponse;
import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.CambiarEstadoIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.ClasificarIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.GeocodificarDireccionRequest;
import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.RegistrarIncidenteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alertacore.incidentservice.domain.model.DespachoRecursoResultado;

import com.alertacore.incidentservice.infrastructure.entry_points.rest.request.DespacharRecursoRequest;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(
        name = "Incidentes",
        description = """
                Registro, consulta, clasificacion, trazabilidad,
                estadisticas y analisis de incidentes.
                """
)
@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {

    private final IncidenteUseCase incidenteUseCase;

    public IncidenteController(
            IncidenteUseCase incidenteUseCase
    ) {
        this.incidenteUseCase =
                Objects.requireNonNull(
                        incidenteUseCase,
                        "El caso de uso de incidentes es obligatorio"
                );
    }
    @Operation(
            summary = "Asignar un recurso al incidente",
            description = """
                Busca el recurso disponible mas cercano del tipo
                solicitado, lo reserva en resource-service y cambia
                el incidente al estado RECURSO_ASIGNADO.
                
                Si falla la actualizacion local despues de reservar,
                incident-service solicita la liberacion compensatoria
                del recurso.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso asignado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            AsignacionRecursoResultado.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud de asignacion invalida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Incidente inexistente o no existen recursos
                        disponibles del tipo solicitado
                        """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                        El incidente no esta clasificado o el recurso
                        dejo de estar disponible antes de reservarlo
                        """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Resource Service no esta disponible",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping(
            "/{incidenteId}/recursos/{recursoId}/despachar"
    )
    public ResponseEntity<DespachoRecursoResultado>
    despacharRecurso(
            @PathVariable
            UUID incidenteId,

            @PathVariable
            UUID recursoId,

            @Valid
            @RequestBody
            DespacharRecursoRequest request
    ) {

        DespachoRecursoResultado resultado =
                incidenteUseCase.despacharRecurso(
                        request.aComando(
                                incidenteId,
                                recursoId
                        )
                );

        return ResponseEntity.ok(
                resultado
        );
    }

    @PostMapping("/{incidenteId}/asignaciones")
    public ResponseEntity<AsignacionRecursoResultado>
    asignarRecurso(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            AsignarRecursoRequest request
    ) {

        AsignacionRecursoResultado resultado =
                incidenteUseCase.asignarRecurso(
                        request.aComando(
                                incidenteId
                        )
                );

        return ResponseEntity.ok(
                resultado
        );
    }

    @Operation(
            summary = "Registrar un incidente",
            description = """
                    Registra un incidente nuevo en estado REPORTADO.
                    Devuelve HTTP 201 Created, el incidente registrado
                    y la ubicacion del recurso en el encabezado Location.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Incidente registrado correctamente",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description =
                                            "Ruta del incidente registrado",
                                    schema = @Schema(
                                            type = "string",
                                            example =
                                                    "/api/v1/incidentes/"
                                                            + "9bb9d4da-c93e-4a54-"
                                                            + "9f44-fd2119662d93"
                                    )
                            )
                    },
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ResultadoIncidente.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del incidente invalidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ResultadoIncidente> registrar(
            @Valid
            @RequestBody
            RegistrarIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                incidenteUseCase.registrarIncidente(
                        request.aComando()
                );

        URI ubicacionRecurso =
                URI.create(
                        "/api/v1/incidentes/"
                                + resultado.id()
                );

        return ResponseEntity
                .created(ubicacionRecurso)
                .body(resultado);
    }

    @Operation(
            summary = "Geocodificar una direccion",
            description = """
                    Convierte una direccion ubicada en Colombia en
                    coordenadas geograficas mediante OpenStreetMap
                    Nominatim. La respuesta incluye la direccion
                    normalizada, latitud, longitud, fuente y atribucion.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Direccion geocodificada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            GeocodificacionResultado.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Direccion vacia, demasiado corta o invalida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description =
                            "No se encontraron coordenadas para la direccion",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description =
                            "El servicio de geocodificacion no esta disponible",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/geocodificar")
    public ResponseEntity<GeocodificacionResultado>
    geocodificar(
            @Valid
            @RequestBody
            GeocodificarDireccionRequest request
    ) {

        GeocodificacionResultado resultado =
                incidenteUseCase.geocodificarDireccion(
                        request.aComando()
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Consultar un incidente por UUID",
            description = """
                    Devuelve la informacion actual de un incidente,
                    incluyendo tipo, descripcion, estado, nivel de
                    riesgo, ubicacion y fechas de registro y
                    actualizacion.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidente encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ResultadoIncidente.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incidente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{incidenteId}")
    public ResponseEntity<ResultadoIncidente>
    consultarPorId(
            @PathVariable
            UUID incidenteId
    ) {

        ResultadoIncidente resultado =
                incidenteUseCase.consultarIncidente(
                        incidenteId
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Listar incidentes",
            description = """
                    Lista los incidentes de forma paginada y ordenada
                    por fecha de reporte descendente. Permite aplicar
                    filtros opcionales por estado y tipo de incidente.
                    El tamanio maximo permitido por pagina es 100.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Pagina de incidentes obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PaginaIncidentesResultado.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Numero de pagina, tamanio, estado o tipo invalido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Paginacion invalida",
                                    value = """
                                            {
                                              "estado": 400,
                                              "codigo": "ARGUMENTO_INVALIDO",
                                              "mensaje": "El tamanio de pagina no puede superar 100",
                                              "ruta": "/api/v1/incidentes",
                                              "erroresValidacion": {}
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PaginaIncidentesResultado> listar(
            @RequestParam(required = false)
            EstadoIncidente estado,

            @RequestParam(required = false)
            TipoIncidente tipo,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        PaginaIncidentesResultado resultado =
                incidenteUseCase.listarIncidentes(
                        estado,
                        tipo,
                        page,
                        size
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Clasificar el riesgo de un incidente",
            description = """
                    Calcula el puntaje y nivel de riesgo del incidente
                    utilizando las reglas del dominio. Requiere que el
                    incidente se encuentre en estado VALIDADO. Si la
                    operacion es exitosa, cambia el estado a CLASIFICADO
                    y registra el movimiento en la linea de tiempo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Incidente clasificado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ResultadoClasificacion.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Solicitud de clasificacion invalida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incidente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description =
                            "El estado actual no permite clasificar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/{incidenteId}/clasificar")
    public ResponseEntity<ResultadoClasificacion> clasificar(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            ClasificarIncidenteRequest request
    ) {
        ResultadoClasificacion resultado =
                incidenteUseCase.clasificarIncidente(
                        request.aComando(
                                incidenteId
                        )
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Consultar la linea de tiempo",
            description = """
                    Devuelve en orden cronologico todos los cambios
                    de estado asociados con el incidente. Cada registro
                    incluye estado anterior, nuevo estado, motivo,
                    responsable y fecha del cambio.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Linea de tiempo obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    RegistroLineaTiempoResultado.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incidente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{incidenteId}/linea-tiempo")
    public ResponseEntity<List<RegistroLineaTiempoResultado>>
    consultarLineaTiempo(
            @PathVariable
            UUID incidenteId
    ) {

        List<RegistroLineaTiempoResultado> resultado =
                incidenteUseCase.consultarLineaTiempo(
                        incidenteId
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cambiar el estado de un incidente",
            description = """
                    Ejecuta una transicion permitida por la maquina
                    de estados del dominio. La actualizacion del
                    incidente y el registro en el historial se realizan
                    dentro de una misma transaccion.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado cambiado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ResultadoIncidente.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nuevo estado o motivo invalido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incidente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description =
                            "Transicion de estado no permitida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @PatchMapping("/{incidenteId}/estado")
    public ResponseEntity<ResultadoIncidente> cambiarEstado(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            CambiarEstadoIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                incidenteUseCase.cambiarEstadoIncidente(
                        request.aComando(
                                incidenteId
                        )
                );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Consultar estadisticas de incidentes",
            description = """
                    Devuelve el total de incidentes, cantidades por
                    estado y nivel de riesgo, incidentes validados y
                    tiempos minimo, promedio y maximo de respuesta
                    inicial.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Estadisticas obtenidas correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            EstadisticasIncidentesResultado.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description =
                            "Error al calcular las estadisticas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/estadisticas/tiempos-respuesta")
    public ResponseEntity<EstadisticasIncidentesResultado>
    consultarEstadisticas() {

        EstadisticasIncidentesResultado resultado =
                incidenteUseCase.consultarEstadisticas();

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Consultar el riesgo meteorologico",
            description = """
                    Consulta las condiciones meteorologicas actuales
                    para las coordenadas del incidente mediante
                    Open-Meteo. Incluye temperatura, precipitacion,
                    lluvia, chubascos, velocidad del viento y nivel
                    de riesgo meteorologico calculado por AlertaCore.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Riesgo meteorologico obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RiesgoMeteorologicoResultado.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incidente no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description =
                            "Open-Meteo no esta disponible o el circuit breaker esta abierto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{incidenteId}/riesgo-meteorologico")
    public ResponseEntity<RiesgoMeteorologicoResultado>
    consultarRiesgoMeteorologico(
            @PathVariable
            UUID incidenteId
    ) {

        RiesgoMeteorologicoResultado resultado =
                incidenteUseCase
                        .consultarRiesgoMeteorologico(
                                incidenteId
                        );

        return ResponseEntity.ok(resultado);
    }
}