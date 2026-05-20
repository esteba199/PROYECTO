package com.agenda.interactiva.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO para estandarizar las respuestas de error de nuestra API REST.
 * Esto ayuda al frontend a entender exactamente qué falló y a mostrar
 * notificaciones visuales claras (por ejemplo, con toasts o alertas).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    // Cuándo ocurrió el error
    private LocalDateTime timestamp;
    
    // Código de estado HTTP (ej: 400, 404, 500)
    private int status;
    
    // Título descriptivo del error (ej: "Not Found", "Bad Request")
    private String error;
    
    // Detalle descriptivo de qué causó el error para facilitar el debugging
    private String message;
    
    // La URL o endpoint donde ocurrió el error (ej: "/api/events/1")
    private String path;
}
