package com.agenda.interactiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la transferencia de información de Notas rápidas.
 */
@Getter
@Setter
public class NoteDTO {

    private Long id;

    @NotBlank(message = "El título de la nota es obligatorio.")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres.")
    private String title;

    private String content;

    private String color; // Código de color hexadecimal
}
