package com.agenda.interactiva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de redirección para Single Page Application (SPA).
 * 
 * En una SPA en React, el enrutamiento es gestionado en el cliente (React Router).
 * Si un usuario refresca la página estando en '/dashboard', el navegador solicita al servidor Spring Boot
 * la ruta '/dashboard'. Como esa ruta no es un endpoint REST, Spring Boot daría un error 404.
 * 
 * Este controlador intercepta esas rutas de navegación del frontend y las redirige (forward)
 * a '/index.html', permitiendo que React tome el control del enrutamiento.
 */
@Controller
public class SpaController {

    @GetMapping(value = {
        "/login",
        "/register",
        "/dashboard",
        "/calendar",
        "/tasks",
        "/notes",
        "/profile"
    })
    public String redirectSpa() {
        return "forward:/index.html";
    }
}
