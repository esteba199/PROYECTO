package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Note.
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // Devuelve todas las notas activas de un usuario.
    List<Note> findByUserId(Long userId);
}
