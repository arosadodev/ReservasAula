package org.example.reservasaula.controller;

import java.util.List;
import org.example.reservasaula.entity.Aula;
import org.example.reservasaula.repository.AulaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aulas")
@CrossOrigin(origins = "*")
public class AulaController {
    private final AulaRepository aulaRepository;

    public AulaController(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Aula>> listar(
            @RequestParam(required = false) Boolean ordenadores,
            @RequestParam(required = false) Integer capacidad) {
        List<Aula> aulas;
        if (ordenadores != null && capacidad != null) {
            aulas = aulaRepository.findAll().stream()
                    .filter(aula -> aula.isOrdenadores() == ordenadores
                            && aula.getCapacidad() > capacidad)
                    .toList();
        } else if (ordenadores != null) {
            aulas = aulaRepository.findByOrdenadores(ordenadores);
        } else if (capacidad != null) {
            aulas = aulaRepository.findByCapacidadGreaterThan(capacidad);
        } else {
            aulas = aulaRepository.findAll();
        }
        return ResponseEntity.ok(aulas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return aulaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Aula no encontrada")));
    }

    @PostMapping
    public ResponseEntity<Aula> crear(@RequestBody Aula aula) {
        aula.setId(null);
        return ResponseEntity.status(201).body(aulaRepository.save(aula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Aula aula) {
        return aulaRepository.findById(id)
                .<ResponseEntity<?>>map(actual -> {
                    actual.setNombre(aula.getNombre());
                    actual.setCapacidad(aula.getCapacidad());
                    actual.setOrdenadores(aula.isOrdenadores());
                    return ResponseEntity.ok(aulaRepository.save(actual));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Aula no encontrada")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!aulaRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new RespuestaError("Aula no encontrada"));
        }
        aulaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
