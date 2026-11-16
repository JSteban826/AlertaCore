package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.ActualizarUbicacionRecursoCommand;
import com.alertacore.resourceservice.application.dto.ActualizarUbicacionRecursoDto;

public interface ActualizarUbicacionRecursoPort {

    ActualizarUbicacionRecursoDto actualizar(
            ActualizarUbicacionRecursoCommand command
    );
}