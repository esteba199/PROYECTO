package com.agenda.interactiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la transferencia de información de Eventos.
 * Permite recibir y enviar datos validados de eventos.
 */
@Getter
@Setter
public class EventDTO {

    private Long id;

    @NotBlank(message = "El título del evento es obligatorio.")
    @Size(max = 100, message = "El título del evento no puede superar los 100 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "La fecha y hora de inicio es obligatoria.")
    private LocalDateTime startTime;

    @NotNull(message = "La fecha y hora de finalización es obligatoria.")
    private LocalDateTime endTime;

    private String location;

    private String color; // Código color hexadecimal (ej: "#66fcf1")

    // Lista opcional de fechas/horas de recordatorios a programar para este evento
    private List<LocalDateTime> reminderTimes;
}
