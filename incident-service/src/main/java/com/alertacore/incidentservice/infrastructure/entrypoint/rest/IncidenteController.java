package com.alertacore.incidentservice.infrastructure.entrypoint.rest;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.domain.model.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.application.port.in.ConsultarEstadisticasIncidentesPort;
import com.alertacore.incidentservice.application.port.in.ConsultarIncidentePort;
import com.alertacore.incidentservice.application.port.in.ConsultarLineaTiempoPort;
import com.alertacore.incidentservice.application.port.in.ConsultarRiesgoMeteorologicoPort;
import com.alertacore.incidentservice.application.port.in.GeocodificarDireccionPort;
import com.alertacore.incidentservice.application.port.in.ListarIncidentesPort;
import com.alertacore.incidentservice.application.port.in.RegistrarIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.exception.ErrorResponse;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.CambiarEstadoIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.ClasificarIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.GeocodificarDireccionRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.RegistrarIncidenteRequest;
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

    private final RegistrarIncidentePort registrarIncidente;
    private final ConsultarIncidentePort consultarIncidente;
    private final ConsultarLineaTiempoPort consultarLineaTiempo;
    private final ListarIncidentesPort listarIncidentes;

    private final ConsultarEstadisticasIncidentesPort
            consultarEstadisticas;

    private final ConsultarRiesgoMeteorologicoPort
            consultarRiesgoMeteorologico;

    private final GeocodificarDireccionPort
            geocodificarDireccion;

    private final ClasificarIncidentePort
            clasificarIncidente;

    private final CambiarEstadoIncidentePort
            cambiarEstadoIncidente;

    public IncidenteController(
            RegistrarIncidentePort registrarIncidente,
            ConsultarIncidentePort consultarIncidente,
            ConsultarLineaTiempoPort consultarLineaTiempo,
            ListarIncidentesPort listarIncidentes,
            ConsultarEstadisticasIncidentesPort
                    consultarEstadisticas,
            ConsultarRiesgoMeteorologicoPort
                    consultarRiesgoMeteorologico,
            GeocodificarDireccionPort geocodificarDireccion,
            ClasificarIncidentePort clasificarIncidente,
            CambiarEstadoIncidentePort cambiarEstadoIncidente
    ) {
        this.registrarIncidente = Objects.requireNonNull(
                registrarIncidente,
                "El caso de uso para registrar es obligatorio"
        );

        this.consultarIncidente = Objects.requireNonNull(
                consultarIncidente,
                "El caso de uso para consultar es obligatorio"
        );

        this.consultarLineaTiempo = Objects.requireNonNull(
                consultarLineaTiempo,
                "El caso de uso para consultar la linea de tiempo es obligatorio"
        );

        this.listarIncidentes = Objects.requireNonNull(
                listarIncidentes,
                "El caso de uso para listar es obligatorio"
        );

        this.consultarEstadisticas =
                Objects.requireNonNull(
                        consultarEstadisticas,
                        "El caso de uso de estadisticas es obligatorio"
                );

        this.consultarRiesgoMeteorologico =
                Objects.requireNonNull(
                        consultarRiesgoMeteorologico,
                        "El caso de uso meteorologico es obligatorio"
                );

        this.geocodificarDireccion =
                Objects.requireNonNull(
                        geocodificarDireccion,
                        "El caso de uso para geocodificar es obligatorio"
                );

        this.clasificarIncidente =
                Objects.requireNonNull(
                        clasificarIncidente,
                        "El caso de uso para clasificar es obligatorio"
                );

        this.cambiarEstadoIncidente =
                Objects.requireNonNull(
                        cambiarEstadoIncidente,
                        "El caso de uso para cambiar estado es obligatorio"
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
                                    description = """
                                            Ruta del incidente registrado
                                            """,
                                    schema = @Schema(
                                            type = "string",
                                            example = "/api/v1/incidentes/9bb9d4da-c93e-4a54-9f44-fd2119662d93"
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
                            ),
                            examples = @ExampleObject(
                                    name = "Solicitud invalida",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 400,
                                              "codigo": "SOLICITUD_INVALIDA",
                                              "mensaje": "La solicitud contiene datos invalidos",
                                              "ruta": "/api/v1/incidentes",
                                              "erroresValidacion": {
                                                "tipo": "El tipo es obligatorio",
                                                "descripcion": "La descripcion debe tener entre 10 y 500 caracteres"
                                              }
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
    @PostMapping
    public ResponseEntity<ResultadoIncidente> registrar(
            @Valid
            @RequestBody
            RegistrarIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                registrarIncidente.ejecutar(
                        request.aComando()
                );

        URI ubicacionRecurso = URI.create(
                "/api/v1/incidentes/" + resultado.id()
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
                    description = """
                            Direccion geocodificada correctamente
                            """,
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
                    description = """
                            Direccion vacia, demasiado corta o invalida
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Direccion invalida",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 400,
                                              "codigo": "SOLICITUD_INVALIDA",
                                              "mensaje": "La solicitud contiene datos invalidos",
                                              "ruta": "/api/v1/incidentes/geocodificar",
                                              "erroresValidacion": {
                                                "direccion": "La direccion debe tener entre 5 y 250 caracteres"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No se encontraron coordenadas para
                            la direccion
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Direccion no encontrada",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 404,
                                              "codigo": "DIRECCION_NO_ENCONTRADA",
                                              "mensaje": "No se encontraron coordenadas para la direccion solicitada",
                                              "ruta": "/api/v1/incidentes/geocodificar",
                                              "erroresValidacion": {}
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = """
                            El servicio de geocodificacion
                            no esta disponible
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = """
                                            Servicio de geocodificacion
                                            no disponible
                                            """,
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 503,
                                              "codigo": "SERVICIO_GEOCODIFICACION_NO_DISPONIBLE",
                                              "mensaje": "No fue posible consultar Nominatim",
                                              "ruta": "/api/v1/incidentes/geocodificar",
                                              "erroresValidacion": {}
                                            }
                                            """
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
                geocodificarDireccion.ejecutar(
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
                            ),
                            examples = @ExampleObject(
                                    name = "Incidente no encontrado",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 404,
                                              "codigo": "INCIDENTE_NO_ENCONTRADO",
                                              "mensaje": "No se encontro el incidente solicitado",
                                              "ruta": "/api/v1/incidentes/11111111-1111-1111-1111-111111111111",
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
    @GetMapping("/{incidenteId}")
    public ResponseEntity<ResultadoIncidente>
    consultarPorId(
            @PathVariable
            UUID incidenteId
    ) {

        ResultadoIncidente resultado =
                consultarIncidente.ejecutar(
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
                    description = """
                            Pagina de incidentes obtenida correctamente
                            """,
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
                    description = """
                            Numero de pagina, tamanio, estado o tipo
                            de incidente invalido
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Paginacion invalida",
                                    summary = """
                                            El tamanio supera el limite
                                            permitido
                                            """,
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
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
                            ),
                            examples = @ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 500,
                                              "codigo": "ERROR_INTERNO",
                                              "mensaje": "Ocurrio un error interno inesperado",
                                              "ruta": "/api/v1/incidentes",
                                              "erroresValidacion": {}
                                            }
                                            """
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
                listarIncidentes.ejecutar(
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
                    description = """
                            Incidente clasificado correctamente
                            """,
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
                    description = """
                            Solicitud de clasificacion invalida
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
                    description = """
                            El estado actual del incidente no permite
                            ejecutar la clasificacion
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = """
                                            Transicion de estado invalida
                                            """,
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 409,
                                              "codigo": "TRANSICION_ESTADO_INVALIDA",
                                              "mensaje": "No es posible clasificar un incidente en estado REPORTADO",
                                              "ruta": "/api/v1/incidentes/11111111-1111-1111-1111-111111111111/clasificar",
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
    @PostMapping("/{incidenteId}/clasificar")
    public ResponseEntity<ResultadoClasificacion> clasificar(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            ClasificarIncidenteRequest request
    ) {
        ResultadoClasificacion resultado =
                clasificarIncidente.ejecutar(
                        request.aComando(incidenteId)
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
                    description = """
                            Linea de tiempo obtenida en orden cronologico
                            """,
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
                consultarLineaTiempo.ejecutar(
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
                    description = """
                            Transicion de estado no permitida
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = "Transicion invalida",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 409,
                                              "codigo": "TRANSICION_ESTADO_INVALIDA",
                                              "mensaje": "No es posible cambiar el incidente de CLASIFICADO a CERRADO",
                                              "ruta": "/api/v1/incidentes/11111111-1111-1111-1111-111111111111/estado",
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
    @PatchMapping("/{incidenteId}/estado")
    public ResponseEntity<ResultadoIncidente> cambiarEstado(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            CambiarEstadoIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                cambiarEstadoIncidente.ejecutar(
                        request.aComando(incidenteId)
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
                    description = """
                            Estadisticas obtenidas correctamente
                            """,
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
                    description = """
                            Error al calcular las estadisticas
                            """,
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
                consultarEstadisticas.ejecutar();

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
                    description = """
                            Riesgo meteorologico obtenido correctamente
                            """,
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
                            ),
                            examples = @ExampleObject(
                                    name = "Incidente no encontrado",
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 404,
                                              "codigo": "INCIDENTE_NO_ENCONTRADO",
                                              "mensaje": "No se encontro el incidente solicitado",
                                              "ruta": "/api/v1/incidentes/11111111-1111-1111-1111-111111111111/riesgo-meteorologico",
                                              "erroresValidacion": {}
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = """
                            Open-Meteo no esta disponible o el
                            circuit breaker se encuentra abierto
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            ErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    name = """
                                            Servicio meteorologico
                                            no disponible
                                            """,
                                    value = """
                                            {
                                              "timestamp": "2026-11-16T12:50:00",
                                              "estado": 503,
                                              "codigo": "SERVICIO_METEOROLOGICO_NO_DISPONIBLE",
                                              "mensaje": "El servicio meteorologico no se encuentra disponible",
                                              "ruta": "/api/v1/incidentes/11111111-1111-1111-1111-111111111111/riesgo-meteorologico",
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
    @GetMapping("/{incidenteId}/riesgo-meteorologico")
    public ResponseEntity<RiesgoMeteorologicoResultado>
    consultarRiesgoMeteorologico(
            @PathVariable
            UUID incidenteId
    ) {

        RiesgoMeteorologicoResultado resultado =
                consultarRiesgoMeteorologico.ejecutar(
                        incidenteId
                );

        return ResponseEntity.ok(resultado);
    }
}