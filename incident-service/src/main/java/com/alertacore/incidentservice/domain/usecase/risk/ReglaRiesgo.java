package com.alertacore.incidentservice.domain.usecase.risk;

import com.alertacore.incidentservice.domain.model.ContextoRiesgo;

public interface ReglaRiesgo {

    int calcularPuntos(ContextoRiesgo contexto);
}