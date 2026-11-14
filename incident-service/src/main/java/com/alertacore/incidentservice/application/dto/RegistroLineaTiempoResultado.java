package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroLineaTiempoResultado(
        UUID id,
        EstadoIncidente estadoAnterior,
        EstadoIncidente nuevoEstado,
        String motivo,
        String responsable,
        LocalDateTime fechaCambio
) {

    public static RegistroLineaTiempoResultado desde(
            RegistroHistorialEstado registro
    ) {
        return new RegistroLineaTiempoResultado(
                registro.id(),
                registro.estadoAnterior(),
                registro.nuevoEstado(),
                registro.motivo(),
                registro.responsable(),
                registro.fechaCambio()
        );
    }
}