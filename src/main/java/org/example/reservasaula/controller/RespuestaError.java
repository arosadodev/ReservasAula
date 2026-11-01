package org.example.reservasaula.controller;

public class RespuestaError {
    private final String mensaje;

    public RespuestaError(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
