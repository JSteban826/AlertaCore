package com.alertacore.resourceservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "despachos_recursos",
        schema = "resources"
)
@Getter
@Setter
@NoArgsConstructor
public class DespachoRecursoEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID recursoId;

    @Column(nullable = false)
    private UUID incidenteId;

    @Column(nullable = false)
    private String responsable;

    @Column(nullable = false)
    private LocalDateTime fechaDespacho;

    private LocalDateTime fechaLlegada;

    private LocalDateTime fechaFinalizacion;
}