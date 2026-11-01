package org.example.reservasaula.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.reservasaula.dto.DatosReserva;
import org.example.reservasaula.entity.Aula;
import org.example.reservasaula.entity.FranjaHoraria;
import org.example.reservasaula.entity.Profesor;
import org.example.reservasaula.entity.Reserva;
import org.example.reservasaula.repository.AulaRepository;
import org.example.reservasaula.repository.FranjaHorariaRepository;
import org.example.reservasaula.repository.ProfesorRepository;
import org.example.reservasaula.repository.ReservaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    private final ReservaRepository reservaRepository;
    private final AulaRepository aulaRepository;
    private final ProfesorRepository profesorRepository;
    private final FranjaHorariaRepository franjaHorariaRepository;

    public ReservaController(
            ReservaRepository reservaRepository,
            AulaRepository aulaRepository,
            ProfesorRepository profesorRepository,
            FranjaHorariaRepository franjaHorariaRepository) {
        this.reservaRepository = reservaRepository;
        this.aulaRepository = aulaRepository;
        this.profesorRepository = profesorRepository;
        this.franjaHorariaRepository = franjaHorariaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listar() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Reserva no encontrada")));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DatosReserva datos) {
        // Comprueba que la petición contiene todos los datos necesarios.
        if (!datos.esValida()) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaError("La reserva debe incluir aula, profesor, franja y fecha"));
        }

        // Impide crear reservas con una fecha anterior al día actual.
        if (datos.fechaReserva.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaError("No se puede reservar una fecha pasada"));
        }

        // Comprueba que el aula está libre en la fecha y franja solicitadas.
        boolean ocupada = reservaRepository.existsByAulaIdAndFechaReservaAndFranjaHorariaId(
                datos.getAulaId(), datos.getFechaReserva(), datos.getFranjaHorariaId());
        if (ocupada) {
            return ResponseEntity.status(409)
                    .body(new RespuestaError("El aula ya está reservada para esa fecha y franja"));
        }

        Aula aula = aulaRepository.findById(datos.getAulaId()).orElse(null);
        Profesor profesor = profesorRepository.findById(datos.getProfesorId()).orElse(null);
        FranjaHoraria franjaHoraria =
                franjaHorariaRepository.findById(datos.getFranjaHorariaId()).orElse(null);
        if (aula == null || profesor == null || franjaHoraria == null) {
            return ResponseEntity.status(404)
                    .body(new RespuestaError("El aula, profesor o franja horaria no existe"));
        }

        Reserva reserva = Reserva.builder()
                .aula(aula)
                .profesor(profesor)
                .franjaHoraria(franjaHoraria)
                .fechaReserva(datos.getFechaReserva())
                .build();
        return ResponseEntity.status(201).body(reservaRepository.save(reserva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DatosReserva datos) {
        return reservaRepository.findById(id)
                .<ResponseEntity<?>>map(actual -> {
                    // Comprueba que la petición contiene todos los datos necesarios.
                    if (!datos.esValida()) {
                        return ResponseEntity.badRequest()
                                .body(new RespuestaError("La reserva debe incluir aula, profesor, franja y fecha"));
                    }

                    // Impide modificar una reserva para asignarle una fecha ya pasada.
                    if (datos.getFechaReserva().isBefore(LocalDate.now())) {
                        return ResponseEntity.badRequest()
                                .body(new RespuestaError("No se puede reservar una fecha pasada"));
                    }

                    Aula aula = aulaRepository.findById(datos.getAulaId()).orElse(null);
                    Profesor profesor = profesorRepository.findById(datos.getProfesorId()).orElse(null);
                    FranjaHoraria franjaHoraria =
                            franjaHorariaRepository.findById(datos.getFranjaHorariaId()).orElse(null);
                    if (aula == null || profesor == null || franjaHoraria == null) {
                        return ResponseEntity.status(404)
                                .body(new RespuestaError("El aula, profesor o franja horaria no existe"));
                    }

                    // Comprueba que la nueva franja está libre, excluyendo la reserva actual.
                    boolean ocupada = reservaRepository
                            .existsByAulaIdAndFechaReservaAndFranjaHorariaIdAndIdNot(
                                    datos.getAulaId(), datos.getFechaReserva(), datos.getFranjaHorariaId(), id);
                    if (ocupada) {
                        return ResponseEntity.status(409)
                                .body(new RespuestaError("El aula ya está reservada para esa fecha y franja"));
                    }

                    actual.setAula(aula);
                    actual.setProfesor(profesor);
                    actual.setFranjaHoraria(franjaHoraria);
                    actual.setFechaReserva(datos.getFechaReserva());
                    return ResponseEntity.ok(reservaRepository.save(actual));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Reserva no encontrada")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new RespuestaError("Reserva no encontrada"));
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
