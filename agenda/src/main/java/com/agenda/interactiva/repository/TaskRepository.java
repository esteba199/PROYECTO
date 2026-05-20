package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Task.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Devuelve todas las tareas activas de un usuario.
    List<Task> findByUserId(Long userId);
}
