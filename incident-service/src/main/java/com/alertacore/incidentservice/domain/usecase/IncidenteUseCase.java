package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.*;
import com.alertacore.incidentservice.domain.model.command.*;
import com.alertacore.incidentservice.domain.model.gateway.TransaccionGateway;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class IncidenteUseCase {

    private final RegistrarIncidenteUseCase registrar;
    private final ConsultarIncidenteUseCase consultar;
    private final ListarIncidentesUseCase listar;

    private final CambiarEstadoIncidenteUseCase cambiarEstado;
    private final ClasificarIncidenteUseCase clasificar;

    private final ConsultarLineaTiempoUseCase lineaTiempo;

    private final ConsultarEstadisticasIncidentesUseCase
            estadisticas;

    private final ConsultarRiesgoMeteorologicoUseCase
            meteorologia;

    private final GeocodificarDireccionUseCase
            geocodificacion;

    private final AsignarRecursoUseCase asignarRecurso;
    private final DespacharRecursoUseCase despacharRecurso;


    private final TransaccionGateway transaccionGateway;

    public IncidenteUseCase(
            RegistrarIncidenteUseCase registrar,
            ConsultarIncidenteUseCase consultar,
            ListarIncidentesUseCase listar,
            CambiarEstadoIncidenteUseCase cambiarEstado,
            ClasificarIncidenteUseCase clasificar,
            ConsultarLineaTiempoUseCase lineaTiempo,
            ConsultarEstadisticasIncidentesUseCase estadisticas,
            ConsultarRiesgoMeteorologicoUseCase meteorologia,
            GeocodificarDireccionUseCase geocodificacion,
            AsignarRecursoUseCase asignarRecurso,
            DespacharRecursoUseCase despacharRecurso,
            TransaccionGateway transaccionGateway
    ) {   this.registrar = Objects.requireNonNull(
                registrar,
                "El caso de uso de registro es obligatorio"
        );

        this.consultar = Objects.requireNonNull(
                consultar,
                "El caso de uso de consulta es obligatorio"
        );

        this.listar = Objects.requireNonNull(
                listar,
                "El caso de uso de listado es obligatorio"
        );

        this.cambiarEstado = Objects.requireNonNull(
                cambiarEstado,
                "El caso de uso de cambio de estado es obligatorio"
        );

        this.clasificar = Objects.requireNonNull(
                clasificar,
                "El caso de uso de clasificacion es obligatorio"
        );

        this.lineaTiempo = Objects.requireNonNull(
                lineaTiempo,
                "El caso de uso de linea de tiempo es obligatorio"
        );

        this.estadisticas = Objects.requireNonNull(
                estadisticas,
                "El caso de uso de estadisticas es obligatorio"
        );

        this.meteorologia = Objects.requireNonNull(
                meteorologia,
                "El caso de uso meteorologico es obligatorio"
        );

        this.geocodificacion = Objects.requireNonNull(
                geocodificacion,
                "El caso de uso de geocodificacion es obligatorio"
        );
        this.asignarRecurso =
                Objects.requireNonNull(
                        asignarRecurso,
                        "El caso de uso de asignacion es obligatorio"
                );
        this.despacharRecurso =
                Objects.requireNonNull(
                        despacharRecurso,
                        "El caso de uso de despacho es obligatorio"
                );

        this.transaccionGateway = Objects.requireNonNull(
                transaccionGateway,
                "El gateway transaccional es obligatorio"
        );
    }

    public ResultadoIncidente registrarIncidente(
            RegistrarIncidenteCommand comando
    ) {
        return registrar.ejecutar(
                comando
        );
    }
    public DespachoRecursoResultado despacharRecurso(
            DespacharRecursoCommand comando
    ) {
        return transaccionGateway.ejecutar(
                () -> despacharRecurso.ejecutar(comando)
        );
    }


    public ResultadoIncidente consultarIncidente(
            UUID incidenteId
    ) {
        return consultar.ejecutar(
                incidenteId
        );
    }

    public PaginaIncidentesResultado listarIncidentes(
            EstadoIncidente estado,
            TipoIncidente tipo,
            int pagina,
            int tamanio
    ) {
        return listar.ejecutar(
                estado,
                tipo,
                pagina,
                tamanio
        );
    }

    public ResultadoIncidente cambiarEstadoIncidente(
            CambiarEstadoIncidenteCommand comando
    ) {
        return transaccionGateway.ejecutar(
                () -> cambiarEstado.ejecutar(
                        comando
                )
        );
    }

    public ResultadoClasificacion clasificarIncidente(
            ClasificarIncidenteCommand comando
    ) {
        return transaccionGateway.ejecutar(
                () -> clasificar.ejecutar(
                        comando
                )
        );
    }

    public List<RegistroLineaTiempoResultado>
    consultarLineaTiempo(
            UUID incidenteId
    ) {

        return lineaTiempo.ejecutar(
                incidenteId
        );
    }

    public EstadisticasIncidentesResultado
    consultarEstadisticas() {

        return estadisticas.ejecutar();
    }

    public RiesgoMeteorologicoResultado
    consultarRiesgoMeteorologico(
            UUID incidenteId
    ) {

        return meteorologia.ejecutar(
                incidenteId
        );
    }

    public GeocodificacionResultado
    geocodificarDireccion(
            GeocodificarDireccionCommand comando
    ) {

        return geocodificacion.ejecutar(
                comando
        );
    }
    public AsignacionRecursoResultado asignarRecurso(
            AsignarRecursoCommand comando
    ) {
        return transaccionGateway.ejecutar(
                () -> asignarRecurso.ejecutar(
                        comando
                )
        );
    }
}