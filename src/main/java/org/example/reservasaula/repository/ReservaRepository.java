package org.example.reservasaula.repository;

import java.time.LocalDate;
import org.example.reservasaula.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByAulaIdAndFechaReservaAndFranjaHorariaId(
            Long aulaId, LocalDate fechaReserva, Long franjaHorariaId);
}
