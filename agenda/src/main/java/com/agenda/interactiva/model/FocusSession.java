package com.agenda.interactiva.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una sesión del Modo Foco (Pomodoro).
 * Permite guardar estadísticas básicas de concentración del usuario.
 */
@Entity
@Table(name = "sesiones_foco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Many-to-One: Muchas sesiones pertenecen a un solo usuario.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private User user;

    // Duración de la sesión en minutos (ej: 25, 50, 90)
    @Column(nullable = false)
    private Integer durationMinutes;

    // Fecha y hora en la que se completó la sesión
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
