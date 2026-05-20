package com.agenda.interactiva.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la respuesta tras un inicio de sesión exitoso.
 * Devuelve el token JWT que el frontend debe guardar y adjuntar en cada petición.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String username;
    private String role;
}
