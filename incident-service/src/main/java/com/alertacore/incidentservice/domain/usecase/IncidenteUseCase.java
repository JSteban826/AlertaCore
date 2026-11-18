
package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.domain.model.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.domain.model.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.model.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class IncidenteUseCase {

    private final RegistrarIncidenteUseCase registrar;
    private final ConsultarIncidenteUseCase consultar;
    private final ListarIncidentesUseCase listar;

    /*
     * Se conservan temporalmente estas interfaces
     * porque sus implementaciones principales son
     * decoradores con @Transactional.
     */
    private final CambiarEstadoIncidentePort cambiarEstado;
    private final ClasificarIncidentePort clasificar;

    private final ConsultarLineaTiempoUseCase lineaTiempo;
    private final ConsultarEstadisticasIncidentesUseCase estadisticas;
    private final ConsultarRiesgoMeteorologicoUseCase meteorologia;
    private final GeocodificarDireccionUseCase geocodificacion;

    public IncidenteUseCase(
            RegistrarIncidenteUseCase registrar,
            ConsultarIncidenteUseCase consultar,
            ListarIncidentesUseCase listar,
            CambiarEstadoIncidentePort cambiarEstado,
            ClasificarIncidentePort clasificar,
            ConsultarLineaTiempoUseCase lineaTiempo,
            ConsultarEstadisticasIncidentesUseCase estadisticas,
            ConsultarRiesgoMeteorologicoUseCase meteorologia,
            GeocodificarDireccionUseCase geocodificacion
    ) {
        this.registrar = Objects.requireNonNull(
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
                "El caso de uso de clasificación es obligatorio"
        );

        this.lineaTiempo = Objects.requireNonNull(
                lineaTiempo,
                "El caso de uso de línea de tiempo es obligatorio"
        );

        this.estadisticas = Objects.requireNonNull(
                estadisticas,
                "El caso de uso de estadísticas es obligatorio"
        );

        this.meteorologia = Objects.requireNonNull(
                meteorologia,
                "El caso de uso meteorológico es obligatorio"
        );

        this.geocodificacion = Objects.requireNonNull(
                geocodificacion,
                "El caso de uso de geocodificación es obligatorio"
        );
    }

    public ResultadoIncidente registrarIncidente(
            RegistrarIncidenteCommand comando
    ) {
        return registrar.ejecutar(comando);
    }

    public ResultadoIncidente consultarIncidente(
            UUID incidenteId
    ) {
        return consultar.ejecutar(incidenteId);
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
        return cambiarEstado.ejecutar(comando);
    }

    public ResultadoClasificacion clasificarIncidente(
            ClasificarIncidenteCommand comando
    ) {
        return clasificar.ejecutar(comando);
    }

    public List<RegistroLineaTiempoResultado> consultarLineaTiempo(
            UUID incidenteId
    ) {
        return lineaTiempo.ejecutar(incidenteId);
    }

    public EstadisticasIncidentesResultado consultarEstadisticas() {
        return estadisticas.ejecutar();
    }

    public RiesgoMeteorologicoResultado consultarRiesgoMeteorologico(
            UUID incidenteId
    ) {
        return meteorologia.ejecutar(incidenteId);
    }

    public GeocodificacionResultado geocodificarDireccion(
            GeocodificarDireccionCommand comando
    ) {
        return geocodificacion.ejecutar(comando);
    }
}
