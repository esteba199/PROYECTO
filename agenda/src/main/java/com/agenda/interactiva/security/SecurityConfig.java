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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuración central de Seguridad usando Sesiones.
 * Adaptado para Thymeleaf y formularios web clásicos.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Define la cadena de filtros de seguridad HTTP (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (Archivos estáticos y páginas de autenticación)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .requestMatchers("/", "/inicio_sesion", "/registro", "/api/auth/register").permitAll()
                // El resto requiere estar autenticado
                .anyRequest().authenticated()
            )
            // Configuración del login mediante formulario de Thymeleaf
            .formLogin(form -> form
                .loginPage("/inicio_sesion") // Ruta a la vista de login (GET)
                .loginProcessingUrl("/inicio_sesion") // Ruta donde se envía el form (POST)
                .defaultSuccessUrl("/panel", true) // Si el login es correcto, va al panel
                .failureUrl("/inicio_sesion?error=true") // Si falla, recarga con error
                .permitAll()
            )
            // Configuración del logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/cerrar_sesion"))
                .logoutSuccessUrl("/inicio_sesion?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * Proveedor de autenticación DAO.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
