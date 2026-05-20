package com.agenda.interactiva.security;

import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado para cargar los detalles del usuario durante el proceso de autenticación.
 * 
 * ¿Por qué se usa esto?
 * Spring Security necesita una forma estándar de buscar usuarios en la base de datos de la aplicación.
 * Implementando la interfaz UserDetailsService, adaptamos nuestro modelo de base de datos (User)
 * al formato que Spring Security entiende (UserDetails).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en la base de datos
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre de usuario: " + username));

        // Adaptamos nuestro objeto User a UserDetails de Spring Security.
        // Mapeamos el rol del usuario a GrantedAuthority.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
