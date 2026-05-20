package com.agenda.interactiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para la transferencia de información de Tareas.
 */
@Getter
@Setter
public class TaskDTO {

    private Long id;

    @NotBlank(message = "El título de la tarea es obligatorio.")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres.")
    private String title;

    private String description;

    private LocalDateTime dueDate;

    private Boolean isCompleted;

    private String priority; // 'LOW', 'MEDIUM', 'HIGH'
}
