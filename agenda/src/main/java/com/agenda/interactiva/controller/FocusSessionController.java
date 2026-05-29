package com.agenda.interactiva.controller;

import com.agenda.interactiva.model.FocusSession;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.FocusSessionRepository;
import com.agenda.interactiva.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST para manejar las sesiones del Modo Foco.
 */
@RestController
@RequestMapping("/api/foco")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionRepository focusSessionRepository;
    private final UserRepository userRepository;

    /**
     * Guarda una nueva sesión completada.
     */
    @PostMapping
    public ResponseEntity<?> saveSession(@RequestBody Map<String, Integer> payload, Authentication auth) {
        Integer duration = payload.get("durationMinutes");
        if (duration == null || duration <= 0) {
            return ResponseEntity.badRequest().body("La duración debe ser mayor a 0");
        }

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        FocusSession session = FocusSession.builder()
                .user(user)
                .durationMinutes(duration)
                .build();
        
        focusSessionRepository.save(session);
        return ResponseEntity.ok(session);
    }

    /**
     * Obtiene las estadísticas del usuario autenticado.
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<FocusSession> sessions = focusSessionRepository.findByUserIdOrderByCompletedAtDesc(user.getId());

        int totalSessions = sessions.size();
        int totalMinutes = sessions.stream().mapToInt(FocusSession::getDurationMinutes).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", totalSessions);
        stats.put("totalMinutes", totalMinutes);
        stats.put("history", sessions); // Devuelve el historial ordenado de más reciente a más antiguo

        return ResponseEntity.ok(stats);
    }
}
