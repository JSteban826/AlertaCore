package com.alertacore.incidentservice.domain.model.gateway;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.model.DespachoRecurso;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import com.alertacore.incidentservice.domain.model.LiberacionRecurso;
import java.util.UUID;

public interface ServicioRecursosGateway {

    RecursoDisponible buscarMasCercano(
            UUID incidenteId,
            TipoRecurso tipoRecurso,
            double latitudIncidente,
            double longitudIncidente,
            double distanciaMaximaKm
    );

    ReservaRecurso reservar(
            UUID recursoId,
            UUID incidenteId,
            int duracionReservaMinutos,
            String idempotencyKey
    );

    DespachoRecurso despachar(
            UUID recursoId,
            UUID incidenteId,
            String responsable
    );

    LiberacionRecurso liberar(
            UUID recursoId,
            UUID incidenteId,
            String motivo
    );
}