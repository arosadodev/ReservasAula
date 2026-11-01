package org.example.reservasaula.dto;

import java.time.LocalDate;
import java.util.Map;

public class DatosReserva {
    public Map<String, Long> aula;
    public Map<String, Long> profesor;
    public Map<String, Long> franjaHoraria;
    public LocalDate fechaReserva;

    public boolean esValida() {
        return fechaReserva != null
                && getAulaId() != null
                && getProfesorId() != null
                && getFranjaHorariaId() != null;
    }

    public Long getAulaId() {
        return obtenerId(aula);
    }

    public Long getProfesorId() {
        return obtenerId(profesor);
    }

    public Long getFranjaHorariaId() {
        return obtenerId(franjaHoraria);
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    private Long obtenerId(Map<String, Long> referencia) {
        return referencia == null ? null : referencia.get("id");
    }
}
