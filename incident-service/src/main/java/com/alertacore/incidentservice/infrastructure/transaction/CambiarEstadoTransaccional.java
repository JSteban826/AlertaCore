package com.alertacore.incidentservice.infrastructure.transaction;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public class CambiarEstadoTransaccional
        implements CambiarEstadoIncidentePort {

    private final CambiarEstadoIncidentePort delegado;

    public CambiarEstadoTransaccional(
            CambiarEstadoIncidentePort delegado
    ) {
        this.delegado = Objects.requireNonNull(
                delegado,
                "El caso de uso delegado es obligatorio"
        );
    }

    @Override
    @Transactional
    public ResultadoIncidente ejecutar(
            CambiarEstadoIncidenteCommand comando
    ) {
        return delegado.ejecutar(comando);
    }
}