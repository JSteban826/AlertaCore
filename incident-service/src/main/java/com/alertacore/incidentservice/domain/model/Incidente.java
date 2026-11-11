package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteInvalidoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Incidente {

    private final UUID id;
    private final TipoIncidente tipo;
    private final String descripcion;
    private final Ubicacion ubicacion;
    private final InformacionContacto informacionContacto;
    private final int cantidadPersonasAfectadas;
    private final boolean hayPersonasHeridas;
    private final LocalDateTime fechaReporte;

    private EstadoIncidente estado;
    private PuntajeRiesgo puntajeRiesgo;
    private LocalDateTime fechaUltimaActualizacion;

    private Incidente(
            UUID id,
            TipoIncidente tipo,
            String descripcion,
            Ubicacion ubicacion,
            InformacionContacto informacionContacto,
            int cantidadPersonasAfectadas,
            boolean hayPersonasHeridas,
            EstadoIncidente estado,
            PuntajeRiesgo puntajeRiesgo,
            LocalDateTime fechaReporte,
            LocalDateTime fechaUltimaActualizacion
    ) {
        validarId(id);
        validarTipo(tipo);
        validarDescripcion(descripcion);
        validarUbicacion(ubicacion);
        validarInformacionContacto(informacionContacto);
        validarCantidadPersonasAfectadas(
                cantidadPersonasAfectadas
        );
        validarEstado(estado);
        validarFechas(
                fechaReporte,
                fechaUltimaActualizacion
        );

        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion.trim();
        this.ubicacion = ubicacion;
        this.informacionContacto = informacionContacto;
        this.cantidadPersonasAfectadas =
                cantidadPersonasAfectadas;
        this.hayPersonasHeridas = hayPersonasHeridas;
        this.estado = estado;
        this.puntajeRiesgo = puntajeRiesgo;
        this.fechaReporte = fechaReporte;
        this.fechaUltimaActualizacion =
                fechaUltimaActualizacion;
    }

    public static Incidente reportar(
            TipoIncidente tipo,
            String descripcion,
            Ubicacion ubicacion,
            InformacionContacto informacionContacto,
            int cantidadPersonasAfectadas,
            boolean hayPersonasHeridas
    ) {
        LocalDateTime fechaActual = LocalDateTime.now();

        return new Incidente(
                UUID.randomUUID(),
                tipo,
                descripcion,
                ubicacion,
                informacionContacto,
                cantidadPersonasAfectadas,
                hayPersonasHeridas,
                EstadoIncidente.REPORTADO,
                null,
                fechaActual,
                fechaActual
        );
    }

    public static Incidente reconstruir(
            UUID id,
            TipoIncidente tipo,
            String descripcion,
            Ubicacion ubicacion,
            InformacionContacto informacionContacto,
            int cantidadPersonasAfectadas,
            boolean hayPersonasHeridas,
            EstadoIncidente estado,
            PuntajeRiesgo puntajeRiesgo,
            LocalDateTime fechaReporte,
            LocalDateTime fechaUltimaActualizacion
    ) {
        return new Incidente(
                id,
                tipo,
                descripcion,
                ubicacion,
                informacionContacto,
                cantidadPersonasAfectadas,
                hayPersonasHeridas,
                estado,
                puntajeRiesgo,
                fechaReporte,
                fechaUltimaActualizacion
        );
    }

    public void validar() {
        cambiarEstado(EstadoIncidente.VALIDADO);
    }

    public void clasificar(PuntajeRiesgo nuevoPuntaje) {
        Objects.requireNonNull(
                nuevoPuntaje,
                "El puntaje de riesgo es obligatorio"
        );

        if (estado != EstadoIncidente.VALIDADO) {
            throw new TransicionEstadoInvalidaException(
                    estado,
                    EstadoIncidente.CLASIFICADO
            );
        }

        this.puntajeRiesgo = nuevoPuntaje;
        cambiarEstado(EstadoIncidente.CLASIFICADO);
    }

    public void asignarRecurso() {
        cambiarEstado(EstadoIncidente.RECURSO_ASIGNADO);
    }

    public void iniciarAtencion() {
        cambiarEstado(EstadoIncidente.EN_ATENCION);
    }

    public void resolver() {
        cambiarEstado(EstadoIncidente.RESUELTO);
    }

    public void cerrar() {
        cambiarEstado(EstadoIncidente.CERRADO);
    }

    public void rechazar() {
        cambiarEstado(EstadoIncidente.RECHAZADO);
    }

    public void cancelar() {
        cambiarEstado(EstadoIncidente.CANCELADO);
    }

    public void escalar() {
        cambiarEstado(EstadoIncidente.ESCALADO);
    }

    public void cambiarEstado(
            EstadoIncidente nuevoEstado
    ) {
        Objects.requireNonNull(
                nuevoEstado,
                "El nuevo estado es obligatorio"
        );

        if (!estado.puedeCambiarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(
                    estado,
                    nuevoEstado
            );
        }

        estado = nuevoEstado;
        fechaUltimaActualizacion = LocalDateTime.now();
    }

    public boolean estaClasificado() {
        return puntajeRiesgo != null;
    }

    public NivelRiesgo obtenerNivelRiesgo() {
        if (puntajeRiesgo == null) {
            return null;
        }

        return puntajeRiesgo.obtenerNivel();
    }

    public int obtenerTiempoMaximoRespuestaMinutos() {
        if (puntajeRiesgo == null) {
            throw new IncidenteInvalidoException(
                    "El incidente debe estar clasificado"
            );
        }

        return puntajeRiesgo
                .obtenerTiempoMaximoRespuestaMinutos();
    }

    private void validarId(UUID id) {
        if (id == null) {
            throw new IncidenteInvalidoException(
                    "El identificador del incidente es obligatorio"
            );
        }
    }

    private void validarTipo(TipoIncidente tipo) {
        if (tipo == null) {
            throw new IncidenteInvalidoException(
                    "El tipo de incidente es obligatorio"
            );
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IncidenteInvalidoException(
                    "La descripcion del incidente es obligatoria"
            );
        }

        String descripcionLimpia = descripcion.trim();

        if (descripcionLimpia.length() < 10) {
            throw new IncidenteInvalidoException(
                    "La descripcion debe tener al menos 10 caracteres"
            );
        }

        if (descripcionLimpia.length() > 500) {
            throw new IncidenteInvalidoException(
                    "La descripcion no puede superar los 500 caracteres"
            );
        }
    }

    private void validarUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IncidenteInvalidoException(
                    "La ubicacion del incidente es obligatoria"
            );
        }
    }

    private void validarInformacionContacto(
            InformacionContacto informacionContacto
    ) {
        if (informacionContacto == null) {
            throw new IncidenteInvalidoException(
                    "La informacion de contacto es obligatoria"
            );
        }
    }

    private void validarCantidadPersonasAfectadas(
            int cantidadPersonasAfectadas
    ) {
        if (cantidadPersonasAfectadas < 0) {
            throw new IncidenteInvalidoException(
                    "La cantidad de personas afectadas no puede ser negativa"
            );
        }
    }

    private void validarEstado(EstadoIncidente estado) {
        if (estado == null) {
            throw new IncidenteInvalidoException(
                    "El estado del incidente es obligatorio"
            );
        }
    }

    private void validarFechas(
            LocalDateTime fechaReporte,
            LocalDateTime fechaUltimaActualizacion
    ) {
        if (fechaReporte == null) {
            throw new IncidenteInvalidoException(
                    "La fecha de reporte es obligatoria"
            );
        }

        if (fechaUltimaActualizacion == null) {
            throw new IncidenteInvalidoException(
                    "La fecha de actualizacion es obligatoria"
            );
        }

        if (fechaUltimaActualizacion.isBefore(fechaReporte)) {
            throw new IncidenteInvalidoException(
                    "La fecha de actualizacion no puede ser anterior al reporte"
            );
        }
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

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public InformacionContacto getInformacionContacto() {
        return informacionContacto;
    }

    public int getCantidadPersonasAfectadas() {
        return cantidadPersonasAfectadas;
    }

    public boolean hayPersonasHeridas() {
        return hayPersonasHeridas;
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public PuntajeRiesgo getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }
}