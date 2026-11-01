package org.example.reservasaula.controller;

import java.util.List;
import org.example.reservasaula.entity.FranjaHoraria;
import org.example.reservasaula.repository.FranjaHorariaRepository;
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
@RequestMapping("/franjas-horarias")
@CrossOrigin(origins = "*")
public class FranjaHorariaController {
    private final FranjaHorariaRepository franjaHorariaRepository;

    public FranjaHorariaController(FranjaHorariaRepository franjaHorariaRepository) {
        this.franjaHorariaRepository = franjaHorariaRepository;
    }

    @GetMapping
    public ResponseEntity<List<FranjaHoraria>> listar() {
        return ResponseEntity.ok(franjaHorariaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return franjaHorariaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Franja horaria no encontrada")));
    }

    @PostMapping
    public ResponseEntity<FranjaHoraria> crear(@RequestBody FranjaHoraria franja) {
        franja.setId(null);
        return ResponseEntity.status(201).body(franjaHorariaRepository.save(franja));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody FranjaHoraria franja) {
        return franjaHorariaRepository.findById(id).<ResponseEntity<?>>map(actual -> {
            actual.setHoraInicio(franja.getHoraInicio());
            actual.setHoraFin(franja.getHoraFin());
            return ResponseEntity.ok(franjaHorariaRepository.save(actual));
        }).orElseGet(() -> ResponseEntity.status(404)
                .body(new RespuestaError("Franja horaria no encontrada")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!franjaHorariaRepository.existsById(id)) {
            return ResponseEntity.status(404)
                    .body(new RespuestaError("Franja horaria no encontrada"));
        }
        franjaHorariaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
