package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.EventDTO;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.Event;
import com.agenda.interactiva.model.Reminder;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para gestionar los Eventos y sus recordatorios asociados.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Obtiene todos los eventos de un usuario específico.
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents(User user) {
        return eventRepository.findByUserId(user.getId());
    }

    /**
     * Obtiene un evento por ID, validando que pertenezca al usuario autenticado.
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long eventId, User user) {
        return eventRepository.findById(eventId)
                .filter(event -> event.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("El evento solicitado no existe o no tienes permisos de acceso."));
    }

    /**
     * Crea un nuevo evento en la agenda y programa sus recordatorios asociados.
     */
    @Transactional
    public Event createEvent(EventDTO dto, User user) {
        Event event = Event.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(dto.getLocation())
                .color(dto.getColor())
                .build();

        // Si se incluyeron recordatorios, los mapeamos y vinculamos al evento.
        if (dto.getReminderTimes() != null) {
            List<Reminder> reminders = dto.getReminderTimes().stream()
                    .map(time -> Reminder.builder()
                            .event(event)
                            .notificationTime(time)
                            .isSent(false)
                            .type("EMAIL")
                            .build())
                    .collect(Collectors.toList());
            event.setReminders(reminders);
        }

        return eventRepository.save(event);
    }

    /**
     * Actualiza la información de un evento existente.
     */
    @Transactional
    public Event updateEvent(Long eventId, EventDTO dto, User user) {
        Event event = getEventById(eventId, user);

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());
        
        if (dto.getColor() != null) {
            event.setColor(dto.getColor());
        }

        // Si se actualizan los recordatorios, reemplazamos la lista actual.
        if (dto.getReminderTimes() != null) {
            event.getReminders().clear();
            List<Reminder> newReminders = dto.getReminderTimes().stream()
                    .map(time -> Reminder.builder()
                            .event(event)
                            .notificationTime(time)
                            .isSent(false)
                            .type("EMAIL")
                            .build())
                    .collect(Collectors.toList());
            event.getReminders().addAll(newReminders);
        }

        return eventRepository.save(event);
    }

    /**
     * Elimina lógicamente un evento de la base de datos (Soft Delete).
     */
    @Transactional
    public void deleteEvent(Long eventId, User user) {
        Event event = getEventById(eventId, user);
        eventRepository.delete(event); // Dispara el soft delete configurado en la clase entidad
    }
}
