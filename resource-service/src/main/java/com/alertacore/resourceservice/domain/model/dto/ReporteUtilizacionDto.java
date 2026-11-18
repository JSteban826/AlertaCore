package com.alertacore.resourceservice.domain.model.dto;

public record ReporteUtilizacionDto(

        long totalRecursos,

        long disponibles,

        long reservados,

        long despachados,

        long enAtencion,

        long fueraDeServicio,

        double porcentajeUtilizacion

) {
}