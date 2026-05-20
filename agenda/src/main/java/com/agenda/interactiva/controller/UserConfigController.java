package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.UserConfigDTO;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.model.UserConfig;
import com.agenda.interactiva.service.UserConfigService;
import com.agenda.interactiva.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar la configuración de usuario (Tema y notificaciones por correo).
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "Configuración", description = "Endpoints para gestionar las preferencias de interfaz y notificaciones del usuario.")
public class UserConfigController {

    private final UserConfigService userConfigService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Obtener configuración", description = "Devuelve el tema actual y estado de alertas por correo.")
    public ResponseEntity<UserConfig> getConfig() {
        User user = userService.getAuthenticatedUser();
        UserConfig config = userConfigService.getOrInitConfig(user);
        return ResponseEntity.ok(config);
    }

    @PutMapping
    @Operation(summary = "Actualizar configuración", description = "Guarda las nuevas preferencias de tema y correo.")
    public ResponseEntity<UserConfig> updateConfig(@RequestBody UserConfigDTO dto) {
        User user = userService.getAuthenticatedUser();
        UserConfig updatedConfig = userConfigService.updateConfig(user, dto);
        return ResponseEntity.ok(updatedConfig);
    }
}
