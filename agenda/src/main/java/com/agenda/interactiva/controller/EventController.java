package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.EventDTO;
import com.agenda.interactiva.model.Event;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.service.EventService;
import com.agenda.interactiva.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar Eventos de la agenda.
 * Protegido por Spring Security, requiere que el usuario esté autenticado.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Endpoints para la gestión de citas y eventos de la agenda.")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los eventos", description = "Devuelve la lista completa de eventos asociados al usuario autenticado.")
    public ResponseEntity<List<Event>> getAllEvents() {
        User user = userService.getAuthenticatedUser();
        List<Event> events = eventService.getAllEvents(user);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un evento por su ID", description = "Devuelve los detalles de un evento en particular si pertenece al usuario.")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        Event event = eventService.getEventById(id, user);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo evento", description = "Registra un nuevo evento y programa sus recordatorios por correo.")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventDTO dto) {
        User user = userService.getAuthenticatedUser();
        Event createdEvent = eventService.createEvent(dto, user);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento", description = "Modifica los datos de un evento existente.")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO dto) {
        User user = userService.getAuthenticatedUser();
        Event updatedEvent = eventService.updateEvent(id, dto, user);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento", description = "Realiza el borrado lógico (soft delete) de un evento.")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        eventService.deleteEvent(id, user);
        return ResponseEntity.noContent().build();
    }
}
