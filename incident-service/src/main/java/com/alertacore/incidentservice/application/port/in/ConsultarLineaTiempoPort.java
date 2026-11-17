package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;

import java.util.List;
import java.util.UUID;

public interface ConsultarLineaTiempoPort {

    List<RegistroLineaTiempoResultado> ejecutar(
            UUID incidenteId
    );
}