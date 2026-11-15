package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.TransicionRecursoInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecursoTest {

    @Test
    void deberiaCrearUnRecursoDisponible() {
        Recurso recurso = crearRecurso();

        assertNotNull(recurso.getId());
        assertEquals("AMB-001", recurso.getCodigo());
        assertEquals(TipoRecurso.AMBULANCIA, recurso.getTipo());
        assertEquals(EstadoRecurso.DISPONIBLE, recurso.getEstado());
        assertTrue(recurso.estaDisponible());
        assertNotNull(recurso.getFechaDisponibleDesde());
    }

    @Test
    void deberiaReservarUnRecursoDisponible() {
        Recurso recurso = crearRecurso();

        recurso.reservar();

        assertEquals(EstadoRecurso.RESERVADO, recurso.getEstado());
        assertFalse(recurso.estaDisponible());
    }

    @Test
    void noDeberiaReservarUnRecursoDosVeces() {
        Recurso recurso = crearRecurso();
        recurso.reservar();

        TransicionRecursoInvalidaException excepcion = assertThrows(
                TransicionRecursoInvalidaException.class,
                recurso::reservar
        );

        assertEquals(
                "TRANSICION_RECURSO_INVALIDA",
                excepcion.getCodigo()
        );
    }

    @Test
    void deberiaDespacharUnRecursoReservado() {
        Recurso recurso = crearRecurso();
        recurso.reservar();

        recurso.despachar();

        assertEquals(EstadoRecurso.DESPACHADO, recurso.getEstado());
    }

    @Test
    void noDeberiaDespacharDirectamenteUnRecursoDisponible() {
        Recurso recurso = crearRecurso();

        assertThrows(
                TransicionRecursoInvalidaException.class,
                recurso::despachar
        );

        assertEquals(EstadoRecurso.DISPONIBLE, recurso.getEstado());
    }

    @Test
    void deberiaPasarDeDespachadoAEnAtencion() {
        Recurso recurso = crearRecurso();
        recurso.reservar();
        recurso.despachar();

        recurso.iniciarAtencion();

        assertEquals(EstadoRecurso.EN_ATENCION, recurso.getEstado());
    }

    @Test
    void deberiaLiberarUnRecursoEnAtencion() {
        Recurso recurso = crearRecurso();
        recurso.reservar();
        recurso.despachar();
        recurso.iniciarAtencion();

        recurso.liberar();

        assertEquals(EstadoRecurso.DISPONIBLE, recurso.getEstado());
        assertNotNull(recurso.getFechaDisponibleDesde());
    }

    @Test
    void noDeberiaLiberarUnRecursoReservado() {
        Recurso recurso = crearRecurso();
        recurso.reservar();

        assertThrows(
                TransicionRecursoInvalidaException.class,
                recurso::liberar
        );
    }

    @Test
    void deberiaPonerUnRecursoFueraDeServicio() {
        Recurso recurso = crearRecurso();

        recurso.ponerFueraDeServicio();

        assertEquals(
                EstadoRecurso.FUERA_DE_SERVICIO,
                recurso.getEstado()
        );
        assertNull(recurso.getFechaDisponibleDesde());
    }

    @Test
    void deberiaReactivarUnRecursoFueraDeServicio() {
        Recurso recurso = crearRecurso();
        recurso.ponerFueraDeServicio();

        recurso.reactivar();

        assertEquals(EstadoRecurso.DISPONIBLE, recurso.getEstado());
        assertNotNull(recurso.getFechaDisponibleDesde());
    }

    @Test
    void deberiaActualizarLaUbicacion() {
        Recurso recurso = crearRecurso();

        UbicacionRecurso nuevaUbicacion =
                UbicacionRecurso.crear(4.6612, -74.0634);

        recurso.actualizarUbicacion(nuevaUbicacion);

        assertSame(nuevaUbicacion, recurso.getUbicacion());
    }

    private Recurso crearRecurso() {
        return Recurso.crear(
                "AMB-001",
                "Ambulancia principal",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(4.6579, -74.0596)
        );
    }
}