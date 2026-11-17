package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.RegistrarIncidentePort;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.gateway.IncidenteGateway;

import java.util.Objects;

public final class RegistrarIncidenteUseCase
        implements RegistrarIncidentePort {

    private final IncidenteGateway repositorio;

    public RegistrarIncidenteUseCase(
            IncidenteGateway repositorio
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de incidentes es obligatorio"
        );
    }

    @Override
    public ResultadoIncidente ejecutar(
            RegistrarIncidenteCommand comando
    ) {
        Objects.requireNonNull(
                comando,
                "El comando de registro es obligatorio"
        );

        Ubicacion ubicacion = new Ubicacion(
                comando.latitud(),
                comando.longitud(),
                comando.direccion()
        );

        InformacionContacto contacto =
                new InformacionContacto(
                        comando.nombreReportante(),
                        comando.correoReportante(),
                        comando.telefonoReportante()
                );

        Incidente incidente = Incidente.reportar(
                comando.tipo(),
                comando.descripcion(),
                ubicacion,
                contacto,
                comando.cantidadPersonasAfectadas(),
                comando.hayPersonasHeridas()
        );

        Incidente incidenteGuardado =
                repositorio.guardar(incidente);

        return ResultadoIncidente.desde(
                incidenteGuardado
        );
    }
}