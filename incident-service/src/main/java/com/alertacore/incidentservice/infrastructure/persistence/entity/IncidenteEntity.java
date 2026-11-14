package com.alertacore.incidentservice.infrastructure.persistence.entity;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "incidentes",
        schema = "incidents"
)
public class IncidenteEntity {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo",
            nullable = false,
            length = 40
    )
    private TipoIncidente tipo;

    @Column(
            name = "descripcion",
            nullable = false,
            length = 500
    )
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 40
    )
    private EstadoIncidente estado;

    @Column(name = "puntaje_riesgo")
    private Integer puntajeRiesgo;

    @Column(
            name = "cantidad_personas_afectadas",
            nullable = false
    )
    private int cantidadPersonasAfectadas;

    @Column(
            name = "hay_personas_heridas",
            nullable = false
    )
    private boolean hayPersonasHeridas;

    @Column(
            name = "latitud",
            nullable = false
    )
    private double latitud;

    @Column(
            name = "longitud",
            nullable = false
    )
    private double longitud;

    @Column(
            name = "direccion",
            nullable = false,
            length = 250
    )
    private String direccion;

    @Column(
            name = "nombre_reportante",
            nullable = false,
            length = 150
    )
    private String nombreReportante;

    @Column(
            name = "correo_reportante",
            nullable = false,
            length = 200
    )
    private String correoReportante;

    @Column(
            name = "telefono_reportante",
            nullable = false,
            length = 30
    )
    private String telefonoReportante;

    @Column(
            name = "fecha_reporte",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaReporte;

    @Column(
            name = "fecha_ultima_actualizacion",
            nullable = false
    )
    private LocalDateTime fechaUltimaActualizacion;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;

    protected IncidenteEntity() {
        // Constructor requerido por JPA.
    }

    public IncidenteEntity(
            UUID id,
            TipoIncidente tipo,
            String descripcion,
            EstadoIncidente estado,
            Integer puntajeRiesgo,
            int cantidadPersonasAfectadas,
            boolean hayPersonasHeridas,
            double latitud,
            double longitud,
            String direccion,
            String nombreReportante,
            String correoReportante,
            String telefonoReportante,
            LocalDateTime fechaReporte,
            LocalDateTime fechaUltimaActualizacion
    ) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.puntajeRiesgo = puntajeRiesgo;
        this.cantidadPersonasAfectadas =
                cantidadPersonasAfectadas;
        this.hayPersonasHeridas = hayPersonasHeridas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.nombreReportante = nombreReportante;
        this.correoReportante = correoReportante;
        this.telefonoReportante = telefonoReportante;
        this.fechaReporte = fechaReporte;
        this.fechaUltimaActualizacion =
                fechaUltimaActualizacion;
    }

    public UUID getId() {
        return id;
    }

    public TipoIncidente getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public Integer getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public int getCantidadPersonasAfectadas() {
        return cantidadPersonasAfectadas;
    }

    public boolean isHayPersonasHeridas() {
        return hayPersonasHeridas;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNombreReportante() {
        return nombreReportante;
    }

    public String getCorreoReportante() {
        return correoReportante;
    }

    public String getTelefonoReportante() {
        return telefonoReportante;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public Long getVersion() {
        return version;
    }
}