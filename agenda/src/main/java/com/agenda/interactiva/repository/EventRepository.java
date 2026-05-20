package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad Event.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Devuelve todos los eventos activos de un usuario específico.
    List<Event> findByUserId(Long userId);
    
    // Devuelve los eventos de un usuario que se encuentren en un rango de fechas.
    List<Event> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
