package org.example.reservasaula.controller;

import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarError(Exception excepcion) {
        LOGGER.error("Error procesando la solicitud", excepcion);
        return ResponseEntity.internalServerError()
                .body(new RespuestaError("No se ha podido procesar la solicitud"));
    }
}
