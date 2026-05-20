package com.agenda.interactiva.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para enviar la información del usuario de forma segura,
 * excluyendo credenciales sensibles de la base de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
