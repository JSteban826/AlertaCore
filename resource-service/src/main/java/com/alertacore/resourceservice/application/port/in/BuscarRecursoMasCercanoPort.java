package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.BuscarRecursoMasCercanoCommand;
import com.alertacore.resourceservice.application.dto.RecursoCercanoDto;

public interface BuscarRecursoMasCercanoPort {

    RecursoCercanoDto buscar(
            BuscarRecursoMasCercanoCommand command
    );
}