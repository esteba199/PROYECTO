package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad FocusSession.
 */
@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    
    // Devuelve todas las sesiones de foco de un usuario
    List<FocusSession> findByUserIdOrderByCompletedAtDesc(Long userId);
}
