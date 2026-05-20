package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad Reminder.
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    // Busca recordatorios pendientes por enviar que ya debieron ser notificados.
    List<Reminder> findByIsSentFalseAndNotificationTimeBefore(LocalDateTime time);
}
