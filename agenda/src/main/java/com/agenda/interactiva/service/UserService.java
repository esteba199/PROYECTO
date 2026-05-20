package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.RegisterRequest;
import com.agenda.interactiva.exception.BadRequestException;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestionar la lógica de negocio de los Usuarios (Registro y perfil).
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConfigService userConfigService;
    private final EmailService emailService;

    /**
     * Registra un nuevo usuario en la base de datos, encriptando su contraseña
     * e inicializando sus configuraciones por defecto.
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya está registrado.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El correo electrónico ya está registrado.");
        }

        // Crear nuevo usuario
        User user = User.builder()
                .username(request.getUsername())
                // Encriptamos la contraseña con BCrypt antes de almacenarla
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role("ROLE_USER") // Rol estándar por defecto
                .build();

        User savedUser = userRepository.save(user);

        // Inicializamos la configuración de tema y alertas para el nuevo usuario
        userConfigService.getOrInitConfig(savedUser);

        // Enviar correo de bienvenida al nuevo usuario
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());

        return savedUser;
    }

    /**
     * Busca un usuario por nombre de usuario.
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con username '" + username + "' no existe."));
    }

    /**
     * Helper para obtener al usuario autenticado actual desde el Contexto de Seguridad de Spring Security.
     */
    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }
}
