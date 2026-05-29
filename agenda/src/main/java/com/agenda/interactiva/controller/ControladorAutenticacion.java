package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.RegisterRequest;
import com.agenda.interactiva.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para manejar la lógica de registro desde Thymeleaf.
 * El login lo maneja automáticamente Spring Security.
 */
@Controller
@RequiredArgsConstructor
public class ControladorAutenticacion {

    private final UserService userService;

    /**
     * Procesa el formulario de registro.
     * @param request Datos del formulario.
     * @param result  Resultados de la validación.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") RegisterRequest request, 
                                   BindingResult result, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        
        // Si hay errores de validación (por ejemplo, correo inválido o contraseña corta)
        if (result.hasErrors()) {
            return "auth/registro"; // Retorna a la misma vista para mostrar errores
        }

        try {
            // Guardamos el usuario en la base de datos
            userService.registerUser(request);
            
            // Si el registro es exitoso, redirigimos al login con un mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro completado! Por favor, inicia sesión.");
            return "redirect:/inicio_sesion";
            
        } catch (Exception e) {
            // Si el usuario o correo ya existen, u otro error
            model.addAttribute("errorRegistro", e.getMessage());
            return "auth/registro";
        }
    }
}
