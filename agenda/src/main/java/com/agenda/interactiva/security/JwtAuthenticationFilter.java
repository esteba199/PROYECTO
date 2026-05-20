package com.agenda.interactiva.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de Autenticación por JWT.
 * 
 * ¿Cómo funciona?
 * Este filtro intercepta CADA petición HTTP entrante antes de que llegue a los controladores.
 * 1. Comprueba si existe un encabezado 'Authorization' que comience con 'Bearer '.
 * 2. Si existe, extrae el token y obtiene el nombre de usuario de él.
 * 3. Valida el token con JwtUtil.
 * 4. Si el token es válido, carga los detalles del usuario desde CustomUserDetailsService
 *    y establece al usuario autenticado en el Contexto de Seguridad de Spring.
 * 5. Esto protege los endpoints e informa a la aplicación sobre quién está haciendo la petición.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si la petición no tiene el encabezado de autorización Bearer, simplemente continúa la cadena de filtros.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token (después de "Bearer ")
        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);

        // Si hay un usuario en el token y este no está autenticado en la sesión actual de Spring Security
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validamos que el token sea legítimo para este usuario
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                // Agregamos detalles adicionales de la petición al token de autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Establecemos al usuario como autenticado globalmente para este hilo de ejecución
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con el procesamiento de la petición
        filterChain.doFilter(request, response);
    }
}
