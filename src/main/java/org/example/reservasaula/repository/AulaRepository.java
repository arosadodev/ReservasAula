package org.example.reservasaula.repository;

import java.util.List;
import org.example.reservasaula.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByOrdenadores(boolean ordenadores);
    List<Aula> findByCapacidadGreaterThan(int capacidad);
}
