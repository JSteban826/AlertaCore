package com.alertacore.resourceservice.infrastructure.driver_adapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "recursos",
        schema = "resources"
)
@Getter
@Setter
@NoArgsConstructor
public class RecursoEntity {

    @Id
    private UUID id;

    @Column(
            nullable = false,
            unique = true,
            length = 50
    )
    private String codigo;

    @Column(
            nullable = false,
            length = 150
    )
    private String nombre;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    private LocalDateTime fechaDisponibleDesde;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    @Version
    private Long version;
}