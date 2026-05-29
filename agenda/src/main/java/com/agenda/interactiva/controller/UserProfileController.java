package com.agenda.interactiva.controller;

import com.agenda.interactiva.model.User;
import com.agenda.interactiva.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para obtener y actualizar datos del usuario autenticado actual.
 * Expone:
 *   - GET  /api/user/me            → Datos del perfil
 *   - PATCH /api/user/me           → Actualizar username/email
 *   - PUT  /api/user/me/password   → Cambiar contraseña
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    /**
     * Devuelve los datos del usuario autenticado:
     * username, email, rol y fecha de creación.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile() {
        User user = userService.getAuthenticatedUser();

        Map<String, Object> profile = new HashMap<>();
        profile.put("username",  user.getUsername());
        profile.put("email",     user.getEmail());
        profile.put("role",      user.getRole());
        profile.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        return ResponseEntity.ok(profile);
    }

    /**
     * Actualiza el nombre de usuario y/o email del usuario autenticado.
     */
    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload) {
        try {
            String newUsername = payload.get("username");
            String newEmail = payload.get("email");

            User updatedUser = userService.updateProfile(newUsername, newEmail);

            Map<String, Object> result = new HashMap<>();
            result.put("username", updatedUser.getUsername());
            result.put("email", updatedUser.getEmail());
            result.put("message", "Perfil actualizado correctamente.");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * Requiere: newPassword y confirmPassword en el body JSON.
     */
    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");
            String confirmPassword = payload.get("confirmPassword");

            if (newPassword == null || newPassword.length() < 8) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La contraseña debe tener al menos 8 caracteres.");
                return ResponseEntity.badRequest().body(error);
            }

            if (!newPassword.equals(confirmPassword)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Las contraseñas no coinciden.");
                return ResponseEntity.badRequest().body(error);
            }

            userService.changePassword(newPassword);

            Map<String, String> result = new HashMap<>();
            result.put("message", "Contraseña actualizada correctamente.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
