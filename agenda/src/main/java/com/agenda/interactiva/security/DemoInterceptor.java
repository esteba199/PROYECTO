package com.agenda.interactiva.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que bloquea operaciones de escritura (POST, PUT, DELETE, PATCH)
 * si el usuario autenticado es el usuario de demostración ("demo").
 *
 * SOLO bloquea rutas de API (/api/**) que modifiquen datos.
 * Las peticiones GET siempre se permiten para que el invitado pueda navegar.
 * Las rutas de login/logout también están excluidas.
 */
@Component
@Slf4j
public class DemoInterceptor implements HandlerInterceptor {

    private static final String DEMO_USERNAME = "demo";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // ── 1. Permitir siempre peticiones de lectura ────────────────────────
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
            return true;
        }

        // ── 2. Permitir rutas de autenticación (login / logout / registro) ───
        if (uri.equals("/inicio_sesion")
                || uri.startsWith("/cerrar_sesion")
                || uri.startsWith("/api/auth/")) {
            return true;
        }

        // ── 3. Solo verificamos usuarios autenticados ────────────────────────
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return true;
        }

        // ── 4. ¿Es el usuario demo? ──────────────────────────────────────────
        if (DEMO_USERNAME.equalsIgnoreCase(auth.getName())) {
            log.warn("[DEMO MODE] Acceso denegado → {} {}", method, uri);

            // Respuesta JSON para llamadas AJAX/API
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                    "{\"error\": true, \"message\": \"Modo Invitado: cuenta de solo lectura. Regístrate para guardar cambios.\"}"
                );
                return false;
            }

            // Para peticiones web normales (formularios), redirigir al panel con flag
            response.sendRedirect("/panel?demoError=true");
            return false;
        }

        return true;
    }
}
