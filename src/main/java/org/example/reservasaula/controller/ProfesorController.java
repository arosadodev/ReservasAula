package org.example.reservasaula.controller;

import java.util.List;
import org.example.reservasaula.entity.Profesor;
import org.example.reservasaula.repository.ProfesorRepository;
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
@RequestMapping("/profesores")
@CrossOrigin(origins = "*")
public class ProfesorController {
    private final ProfesorRepository profesorRepository;

    public ProfesorController(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Profesor>> listar() {
        return ResponseEntity.ok(profesorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return profesorRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new RespuestaError("Profesor no encontrado")));
    }

    @PostMapping
    public ResponseEntity<Profesor> crear(@RequestBody Profesor profesor) {
        profesor.setId(null);
        return ResponseEntity.status(201).body(profesorRepository.save(profesor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Profesor profesor) {
        return profesorRepository.findById(id).<ResponseEntity<?>>map(actual -> {
            actual.setNombre(profesor.getNombre());
            actual.setApellidos(profesor.getApellidos());
            actual.setEmail(profesor.getEmail());
            return ResponseEntity.ok(profesorRepository.save(actual));
        }).orElseGet(() -> ResponseEntity.status(404)
                .body(new RespuestaError("Profesor no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!profesorRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new RespuestaError("Profesor no encontrado"));
        }
        profesorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
