package com.alertacore.incidentservice.domain.enums;

import java.util.Set;

public enum EstadoIncidente {

    REPORTADO,
    VALIDADO,
    CLASIFICADO,
    RECURSO_ASIGNADO,
    EN_ATENCION,
    RESUELTO,
    CERRADO,
    RECHAZADO,
    CANCELADO,
    ESCALADO;

    public boolean puedeCambiarA(EstadoIncidente nuevoEstado) {
        if (nuevoEstado == null) {
            return false;
        }

        return transicionesPermitidas().contains(nuevoEstado);
    }

    private Set<EstadoIncidente> transicionesPermitidas() {
        return switch (this) {
            case REPORTADO ->
                    Set.of(VALIDADO, RECHAZADO, CANCELADO);

            case VALIDADO ->
                    Set.of(CLASIFICADO, RECHAZADO, CANCELADO);

            case CLASIFICADO ->
                    Set.of(RECURSO_ASIGNADO, ESCALADO, CANCELADO);

            case RECURSO_ASIGNADO ->
                    Set.of(EN_ATENCION, ESCALADO, CANCELADO);

            case EN_ATENCION ->
                    Set.of(RESUELTO, ESCALADO);

            case ESCALADO ->
                    Set.of(RECURSO_ASIGNADO, EN_ATENCION, RESUELTO);

            case RESUELTO ->
                    Set.of(CERRADO, EN_ATENCION);

            case CERRADO, RECHAZADO, CANCELADO ->
                    Set.of();
        };
    }
}