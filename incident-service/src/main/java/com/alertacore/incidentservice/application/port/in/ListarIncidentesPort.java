package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.dto.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;

public interface ListarIncidentesPort {

    PaginaIncidentesResultado ejecutar(
            EstadoIncidente estado,
            TipoIncidente tipo,
            int pagina,
            int tamanio
    );
}