package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ReservaRecurso {

    private final UUID id;

    private final UUID recursoId;

    private final UUID incidenteId;

    private final String claveIdempotencia;

    private final LocalDateTime fechaReserva;

    private final LocalDateTime fechaVencimiento;

    private EstadoReserva estadoReserva;

    public ReservaRecurso(
            UUID id,
            UUID recursoId,
            UUID incidenteId,
            String claveIdempotencia,
            LocalDateTime fechaReserva,
            LocalDateTime fechaVencimiento,
            EstadoReserva estadoReserva
    ) {

        this.id = Objects.requireNonNull(
                id,
                "El id es obligatorio"
        );

        this.recursoId = Objects.requireNonNull(
                recursoId,
                "El recurso es obligatorio"
        );

        this.incidenteId = Objects.requireNonNull(
                incidenteId,
                "El incidente es obligatorio"
        );

        this.claveIdempotencia = validarTexto(
                claveIdempotencia,
                "La clave de idempotencia es obligatoria"
        );

        this.fechaReserva = Objects.requireNonNull(
                fechaReserva,
                "La fecha de reserva es obligatoria"
        );

        this.fechaVencimiento = Objects.requireNonNull(
                fechaVencimiento,
                "La fecha de vencimiento es obligatoria"
        );

        this.estadoReserva = Objects.requireNonNull(
                estadoReserva,
                "El estado es obligatorio"
        );

        validarFechas();
    }

    public static ReservaRecurso crear(
            UUID recursoId,
            UUID incidenteId,
            String claveIdempotencia,
            int duracionReservaMinutos
    ) {

        if (duracionReservaMinutos <= 0) {
            throw new IllegalArgumentException(
                    "La duración de la reserva debe ser mayor que cero"
            );
        }

        LocalDateTime ahora = LocalDateTime.now();

        return new ReservaRecurso(
                UUID.randomUUID(),
                recursoId,
                incidenteId,
                claveIdempotencia,
                ahora,
                ahora.plusMinutes(duracionReservaMinutos),
                EstadoReserva.ACTIVA
        );
    }

    public boolean estaActiva() {

        return estadoReserva == EstadoReserva.ACTIVA
                && !estaVencida();
    }

    public boolean estaVencida() {

        return estadoReserva == EstadoReserva.VENCIDA
                || LocalDateTime.now().isAfter(
                fechaVencimiento
        );
    }

    public void vencer() {

        estadoReserva = EstadoReserva.VENCIDA;
    }

    public void cancelar() {

        estadoReserva = EstadoReserva.CANCELADA;
    }

    private void validarFechas() {

        if (!fechaVencimiento.isAfter(fechaReserva)) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento debe ser posterior a la fecha de reserva"
            );
        }
    }

    private static String validarTexto(
            String valor,
            String mensaje
    ) {

        if (valor == null || valor.isBlank()) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }

        return valor.trim();
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

    public String getClaveIdempotencia() {
        return claveIdempotencia;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }
}