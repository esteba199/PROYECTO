package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.AuthRequest;
import com.agenda.interactiva.dto.AuthResponse;
import com.agenda.interactiva.dto.RegisterRequest;
import com.agenda.interactiva.dto.UserResponse;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.security.JwtUtil;
import com.agenda.interactiva.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la Autenticación (Registro e Inicio de Sesión).
 * Excluido de los filtros de seguridad de Spring (público).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para registrarse e iniciar sesión por JWT.")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una cuenta de usuario y le inicializa las preferencias por defecto.")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        
        UserResponse response = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales encriptadas y genera un Token JWT válido por 24 horas.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // Solicitamos a Spring Security validar las credenciales ingresadas.
        // Si no coinciden, se arrojará una excepción automáticamente (BadCredentialsException) que será interceptada en ExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Si la autenticación es correcta, recuperamos los detalles completos del usuario
        User user = userService.findByUsername(request.getUsername());

        // Generamos el Token JWT firmado
        String token = jwtUtil.generateToken(user.getUsername());

        // Retornamos el token al frontend
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }
}
