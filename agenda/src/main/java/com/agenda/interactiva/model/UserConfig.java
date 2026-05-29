package com.agenda.interactiva.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa las preferencias y configuraciones del usuario.
 * Relación One-to-One con la entidad User.
 */
@Entity
@Table(name = "configuraciones_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación One-to-One inversa.
    // Explicación: @JoinColumn especifica la columna de clave foránea en la tabla ('usuario_id').
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(length = 20)
    private String theme; // 'LIGHT' o 'DARK'

    @Column(length = 10)
    private String currency; // 'USD', 'EUR', etc.

    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.theme == null) {
            this.theme = "DARK"; // Modo oscuro por defecto
        }
        if (this.currency == null) {
            this.currency = "USD";
        }
        if (this.emailNotificationsEnabled == null) {
            this.emailNotificationsEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
