package com.agenda.interactiva.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de inicio de sesión (Login).
 * Contiene anotaciones de validación para asegurar que los datos no estén vacíos.
 */
@Getter
@Setter
public class AuthRequest {

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;
}
