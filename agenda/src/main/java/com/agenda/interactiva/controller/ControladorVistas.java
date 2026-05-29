package com.agenda.interactiva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Controlador principal para las vistas de Thymeleaf.
 * A diferencia de @RestController, @Controller devuelve nombres de plantillas HTML
 * que Thymeleaf renderizará en el servidor.
 */
@Controller
public class ControladorVistas {

    /**
     * Redirige la raíz "/" al panel de control si está autenticado,
     * o al inicio de sesión si no lo está.
     */
    @GetMapping("/")
    public String raiz() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/panel";
        }
        return "redirect:/inicio_sesion";
    }

    // ==========================================
    // VISTAS DE AUTENTICACIÓN
    // ==========================================

    @GetMapping("/inicio_sesion")
    public String inicioSesion() {
        // Devuelve el archivo src/main/resources/templates/auth/inicio_sesion.html
        return "auth/inicio_sesion";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        // Inyectamos un objeto vacío para que Thymeleaf pueda enlazar los campos del formulario
        model.addAttribute("usuario", new com.agenda.interactiva.dto.RegisterRequest());
        // Devuelve el archivo src/main/resources/templates/auth/registro.html
        return "auth/registro";
    }

    // ==========================================
    // VISTAS DEL DASHBOARD (Requieren estar autenticado)
    // ==========================================

    @GetMapping("/panel")
    public String panel(Model model) {
        // Aquí podríamos enviar datos desde la Base de Datos a la vista usando el objeto Model.
        // model.addAttribute("nombreAtributo", valor);
        return "dashboard/panel";
    }

    @GetMapping("/calendario")
    public String calendario() {
        return "dashboard/calendario";
    }

    @GetMapping("/tareas")
    public String tareas() {
        return "dashboard/tareas";
    }

    @GetMapping("/notas")
    public String notas() {
        return "dashboard/notas";
    }

    @GetMapping("/finanzas")
    public String finanzas() {
        return "dashboard/finanzas";
    }
    
    @GetMapping("/perfil")
    public String perfil() {
        return "dashboard/perfil";
    }

    @GetMapping("/foco")
    public String foco() {
        return "dashboard/foco";
    }
}
