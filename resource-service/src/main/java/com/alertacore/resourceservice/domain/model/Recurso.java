package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.TransicionRecursoInvalidaException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Recurso {

    private final UUID id;
    private final String codigo;
    private String nombre;
    private final TipoRecurso tipo;
    private EstadoRecurso estado;
    private UbicacionRecurso ubicacion;
    private LocalDateTime fechaDisponibleDesde;
    private LocalDateTime fechaUltimaActualizacion;

    public Recurso(
            UUID id,
            String codigo,
            String nombre,
            TipoRecurso tipo,
            EstadoRecurso estado,
            UbicacionRecurso ubicacion,
            LocalDateTime fechaDisponibleDesde,
            LocalDateTime fechaUltimaActualizacion
    ) {
        this.id = Objects.requireNonNull(id, "El id es obligatorio");
        this.codigo = validarTexto(codigo, "El código es obligatorio");
        this.nombre = validarTexto(nombre, "El nombre es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "El tipo es obligatorio");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
        this.ubicacion = Objects.requireNonNull(
                ubicacion,
                "La ubicación es obligatoria"
        );
        this.fechaDisponibleDesde = fechaDisponibleDesde;
        this.fechaUltimaActualizacion = Objects.requireNonNull(
                fechaUltimaActualizacion,
                "La fecha de última actualización es obligatoria"
        );
    }

    public static Recurso crear(
            String codigo,
            String nombre,
            TipoRecurso tipo,
            UbicacionRecurso ubicacion
    ) {
        LocalDateTime ahora = LocalDateTime.now();

        return new Recurso(
                UUID.randomUUID(),
                codigo,
                nombre,
                tipo,
                EstadoRecurso.DISPONIBLE,
                ubicacion,
                ahora,
                ahora
        );
    }

    public boolean estaDisponible() {
        return estado == EstadoRecurso.DISPONIBLE;
    }

    public void reservar() {
        cambiarEstadoDesde(
                EstadoRecurso.DISPONIBLE,
                EstadoRecurso.RESERVADO
        );
    }

    public void despachar() {
        cambiarEstadoDesde(
                EstadoRecurso.RESERVADO,
                EstadoRecurso.DESPACHADO
        );
    }

    public void iniciarAtencion() {
        cambiarEstadoDesde(
                EstadoRecurso.DESPACHADO,
                EstadoRecurso.EN_ATENCION
        );
    }

    public void liberar() {
        if (estado != EstadoRecurso.DESPACHADO
                && estado != EstadoRecurso.EN_ATENCION) {
            throw new TransicionRecursoInvalidaException(
                    estado,
                    EstadoRecurso.DISPONIBLE
            );
        }

        estado = EstadoRecurso.DISPONIBLE;
        fechaDisponibleDesde = LocalDateTime.now();
        actualizarFechaModificacion();
    }

    public void ponerFueraDeServicio() {
        if (estado == EstadoRecurso.FUERA_DE_SERVICIO) {
            return;
        }

        estado = EstadoRecurso.FUERA_DE_SERVICIO;
        fechaDisponibleDesde = null;
        actualizarFechaModificacion();
    }

    public void reactivar() {
        cambiarEstadoDesde(
                EstadoRecurso.FUERA_DE_SERVICIO,
                EstadoRecurso.DISPONIBLE
        );

        fechaDisponibleDesde = LocalDateTime.now();
    }

    public void actualizarUbicacion(UbicacionRecurso nuevaUbicacion) {
        this.ubicacion = Objects.requireNonNull(
                nuevaUbicacion,
                "La nueva ubicación es obligatoria"
        );

        actualizarFechaModificacion();
    }

    private void cambiarEstadoDesde(
            EstadoRecurso estadoEsperado,
            EstadoRecurso estadoDestino
    ) {
        if (estado != estadoEsperado) {
            throw new TransicionRecursoInvalidaException(
                    estado,
                    estadoDestino
            );
        }

        estado = estadoDestino;
        actualizarFechaModificacion();
    }

    private void actualizarFechaModificacion() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }

    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }

        return valor.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoRecurso getTipo() {
        return tipo;
    }

    public EstadoRecurso getEstado() {
        return estado;
    }

    public UbicacionRecurso getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getFechaDisponibleDesde() {
        return fechaDisponibleDesde;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }
}