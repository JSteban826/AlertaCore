package com.alertacore.incidentservice.infrastructure.persistence.entity;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "historial_estados_incidente",
        schema = "incidents",
        indexes = {
                @Index(
                        name = "idx_historial_incidente",
                        columnList = "incidente_id"
                ),
                @Index(
                        name = "idx_historial_fecha",
                        columnList = "fecha_cambio"
                )
        }
)
public class HistorialEstadoIncidenteEntity {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "incidente_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_historial_incidente"
            )
    )
    private IncidenteEntity incidente;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_anterior",
            length = 40
    )
    private EstadoIncidente estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "nuevo_estado",
            nullable = false,
            length = 40
    )
    private EstadoIncidente nuevoEstado;

    @Column(
            name = "motivo",
            nullable = false,
            length = 250
    )
    private String motivo;

    @Column(
            name = "responsable",
            length = 150
    )
    private String responsable;

    @Column(
            name = "fecha_cambio",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaCambio;

    protected HistorialEstadoIncidenteEntity() {
        // Constructor requerido por JPA.
    }

    public HistorialEstadoIncidenteEntity(
            UUID id,
            IncidenteEntity incidente,
            EstadoIncidente estadoAnterior,
            EstadoIncidente nuevoEstado,
            String motivo,
            String responsable,
            LocalDateTime fechaCambio
    ) {
        this.id = Objects.requireNonNull(id);
        this.incidente = Objects.requireNonNull(incidente);
        this.estadoAnterior = estadoAnterior;
        this.nuevoEstado = Objects.requireNonNull(nuevoEstado);
        this.motivo = Objects.requireNonNull(motivo);
        this.responsable = responsable;
        this.fechaCambio = Objects.requireNonNull(fechaCambio);
    }

    public UUID getId() {
        return id;
    }

    public IncidenteEntity getIncidente() {
        return incidente;
    }

    public EstadoIncidente getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoIncidente getNuevoEstado() {
        return nuevoEstado;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getResponsable() {
        return responsable;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
}