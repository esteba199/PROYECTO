package com.agenda.interactiva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para peticiones incorrectas (HTTP 400).
 * Se lanza cuando los datos enviados no cumplen las validaciones de negocio,
 * como contraseñas que no coinciden o correos electrónicos duplicados.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}
