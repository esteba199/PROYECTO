package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * Proporciona métodos CRUD básicos y consultas personalizadas para la autenticación.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Busca un usuario activo por su nombre de usuario.
    Optional<User> findByUsername(String username);
    
    // Busca un usuario activo por su correo electrónico.
    Optional<User> findByEmail(String email);
    
    // Verifica si un nombre de usuario ya está registrado en el sistema.
    boolean existsByUsername(String username);
    
    // Verifica si un correo electrónico ya está registrado en el sistema.
    boolean existsByEmail(String email);
}
