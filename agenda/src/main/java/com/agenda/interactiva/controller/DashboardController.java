package com.agenda.interactiva.controller;

import com.agenda.interactiva.model.Event;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.EventRepository;
import com.agenda.interactiva.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el Dashboard.
 * Provee endpoints agregados que combinan datos de múltiples módulos
 * para construir los widgets del panel principal.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final EventRepository eventRepository;

    /**
     * Devuelve un resumen de actividad reciente para el widget del panel.
     * Combina los próximos 3 eventos de la agenda del usuario autenticado.
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        User user = userService.getAuthenticatedUser();

        // Obtener los próximos eventos a partir de ahora
        List<Event> todosLosEventos = eventRepository.findByUserId(user.getId());
        
        // Filtrar solo los que aún no han ocurrido y ordenar por fecha
        LocalDateTime ahora = LocalDateTime.now();
        List<Map<String, Object>> proximosEventos = new LinkedList<>();

        todosLosEventos.stream()
                .filter(e -> e.getStartTime() != null && e.getStartTime().isAfter(ahora))
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .limit(5)
                .forEach(e -> {
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("id", e.getId());
                    eventData.put("title", e.getTitle());
                    eventData.put("startTime", e.getStartTime().toString());
                    eventData.put("location", e.getLocation() != null ? e.getLocation() : "");
                    eventData.put("color", e.getColor() != null ? e.getColor() : "#3498db");
                    proximosEventos.add(eventData);
                });

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("username", user.getUsername());
        resumen.put("proximosEventos", proximosEventos);
        resumen.put("totalEventos", todosLosEventos.size());

        return ResponseEntity.ok(resumen);
    }
}
