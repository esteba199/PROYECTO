package com.agenda.interactiva.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Seguridad (Spring Security).
 * 
 * Anotaciones:
 * - @Configuration: Define que esta clase contiene Beans de configuración de Spring.
 * - @EnableWebSecurity: Habilita la seguridad web en la aplicación.
 * - @EnableMethodSecurity: Permite el control de acceso fino usando @PreAuthorize("hasRole('ADMIN')") etc.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Define la cadena de filtros de seguridad HTTP (SecurityFilterChain).
     * Aquí configuramos qué rutas son públicas, cuáles requieren autenticación,
     * desactivamos CSRF y configuramos las sesiones sin estado.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF (Cross-Site Request Forgery) porque no usamos cookies de sesión.
            // Los JWT son autoejecutables y no vulnerables a CSRF estándar de sesión tradicional.
            .csrf(csrf -> csrf.disable())
            
            // Indicamos que la sesión no debe guardar estados en el servidor (Stateless).
            // Cada petición debe enviar su token de forma independiente.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de login y registro
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints de Swagger/OpenAPI abiertos para pruebas
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Recursos estáticos del frontend (React compilado en resources/static)
                .requestMatchers("/", "/index.html", "/assets/**", "/favicon.svg", "/icons.svg").permitAll()
                
                // Rutas mapeadas en SpaController para la navegación frontend
                .requestMatchers("/login", "/register", "/dashboard", "/calendar", "/tasks", "/notes", "/profile").permitAll()
                
                // Cualquier endpoint REST bajo /api/** requiere autenticación
                .requestMatchers("/api/**").authenticated()
                
                // Cualquier otra solicitud se permite para evitar bloqueos de archivos estáticos
                .anyRequest().permitAll()
            )
            
            // Establecemos el proveedor de autenticación
            .authenticationProvider(authenticationProvider())
            
            // Añadimos nuestro filtro JWT antes del filtro de autenticación estándar de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de autenticación DAO.
     * Enlaza nuestra base de datos (a través de UserDetailsService) y el encriptador de contraseñas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestor de autenticación oficial para coordinar el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encriptador BCrypt.
     * Utiliza algoritmos hashing seguros e irreversibles de factor variable para cifrar las contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
