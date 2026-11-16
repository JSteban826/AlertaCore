package com.alertacore.resourceservice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class DespachoRecurso {

    private final UUID id;

    private final UUID recursoId;

    private final UUID incidenteId;

    private final String responsable;

    private final LocalDateTime fechaDespacho;

    private LocalDateTime fechaLlegada;

    private LocalDateTime fechaFinalizacion;

    public DespachoRecurso(
            UUID id,
            UUID recursoId,
            UUID incidenteId,
            String responsable,
            LocalDateTime fechaDespacho,
            LocalDateTime fechaLlegada,
            LocalDateTime fechaFinalizacion
    ) {

        this.id = Objects.requireNonNull(id);

        this.recursoId = Objects.requireNonNull(recursoId);

        this.incidenteId = Objects.requireNonNull(incidenteId);

        this.responsable = Objects.requireNonNull(responsable);

        this.fechaDespacho =
                Objects.requireNonNull(fechaDespacho);

        this.fechaLlegada = fechaLlegada;

        this.fechaFinalizacion = fechaFinalizacion;
    }

    public static DespachoRecurso crear(
            UUID recursoId,
            UUID incidenteId,
            String responsable
    ) {

        return new DespachoRecurso(
                UUID.randomUUID(),
                recursoId,
                incidenteId,
                responsable,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public void registrarLlegada() {
        this.fechaLlegada = LocalDateTime.now();
    }

    public void finalizar() {
        this.fechaFinalizacion = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRecursoId() {
        return recursoId;
    }

    public UUID getIncidenteId() {
        return incidenteId;
    }

    public String getResponsable() {
        return responsable;
    }

    public LocalDateTime getFechaDespacho() {
        return fechaDespacho;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }
}