package com.agenda.interactiva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para representar recursos no encontrados (HTTP 404).
 * Se lanza cuando intentamos buscar una tarea, nota, evento o usuario que no existe
 * o fue eliminado lógicamente.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
