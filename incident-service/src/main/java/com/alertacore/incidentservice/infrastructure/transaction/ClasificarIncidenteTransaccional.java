package com.alertacore.incidentservice.infrastructure.transaction;

import com.alertacore.incidentservice.application.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public class ClasificarIncidenteTransaccional
        implements ClasificarIncidentePort {

    private final ClasificarIncidentePort delegado;

    public ClasificarIncidenteTransaccional(
            ClasificarIncidentePort delegado
    ) {
        this.delegado = Objects.requireNonNull(
                delegado,
                "El caso de uso delegado es obligatorio"
        );
    }

    @Override
    @Transactional
    public ResultadoClasificacion ejecutar(
            ClasificarIncidenteCommand comando
    ) {
        return delegado.ejecutar(comando);
    }
}