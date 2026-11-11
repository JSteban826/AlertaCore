package com.alertacore.incidentservice.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public final class InformacionContacto {

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final String nombreReportante;
    private final String correo;
    private final String telefono;

    public InformacionContacto(
            String nombreReportante,
            String correo,
            String telefono
    ) {
        validarNombreReportante(nombreReportante);
        validarCorreo(correo);
        validarTelefono(telefono);

        this.nombreReportante = nombreReportante.trim();
        this.correo = correo.trim().toLowerCase();
        this.telefono = telefono.trim();
    }

    private void validarNombreReportante(String nombreReportante) {
        if (nombreReportante == null || nombreReportante.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del reportante no puede estar vacio"
            );
        }
    }

    private void validarCorreo(String correo) {
        if (correo == null
                || correo.isBlank()
                || !PATRON_CORREO.matcher(correo.trim()).matches()) {

            throw new IllegalArgumentException(
                    "El correo del reportante no es valido"
            );
        }
    }

    private void validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException(
                    "El telefono del reportante no puede estar vacio"
            );
        }

        String telefonoNormalizado =
                telefono.replaceAll("[\\s+-]", "");

        if (!telefonoNormalizado.matches("\\d{7,15}")) {
            throw new IllegalArgumentException(
                    "El telefono debe contener entre 7 y 15 digitos"
            );
        }
    }

    public String getNombreReportante() {
        return nombreReportante;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof InformacionContacto contacto)) {
            return false;
        }

        return nombreReportante.equals(contacto.nombreReportante)
                && correo.equals(contacto.correo)
                && telefono.equals(contacto.telefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreReportante, correo, telefono);
    }
}