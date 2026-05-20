package com.agenda.interactiva.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa a un Usuario en la base de datos.
 * 
 * Explicación de anotaciones:
 * - @Entity: Indica a Spring Data JPA que esta clase se mapea con una tabla de la BD.
 * - @Table(name = "usuarios"): Especifica el nombre exacto de la tabla.
 * - @SQLDelete: Modifica el comportamiento de delete para realizar un borrado lógico (Soft Delete),
 *   actualizando la columna 'deleted_at' en lugar de borrar la fila físicamente.
 * - @SQLRestriction: Filtra automáticamente todas las consultas SELECT para que solo devuelvan
 *   usuarios activos (donde 'deleted_at' sea nulo).
 */
@Entity
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // Marcamos con @JsonIgnore para que el hash de la contraseña nunca viaje en los JSON de respuesta de la API.
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String role; // Ej: 'ROLE_USER', 'ROLE_ADMIN'

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relaciones JPA
    
    // Un usuario tiene una única configuración. CascadeType.ALL propaga cambios.
    // orphanRemoval = true borra la configuración si se desvincula del usuario.
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserConfig config;

    // Relación One-to-Many: Un usuario puede tener múltiples eventos.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Event> events = new ArrayList<>();

    // Relación One-to-Many: Un usuario puede tener múltiples tareas.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    // Relación One-to-Many: Un usuario puede tener múltiples notas.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Note> notes = new ArrayList<>();

    // Método de ciclo de vida JPA para establecer la fecha de creación automáticamente.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = "ROLE_USER"; // Rol por defecto
        }
    }

    // Método de ciclo de vida JPA para actualizar la fecha de modificación antes de cada UPDATE.
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
