package com.agenda.interactiva.config;

import com.agenda.interactiva.security.DemoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración central de Web MVC.
 * Sirve para registrar interceptores, CORS globales (si no usamos @CrossOrigin), etc.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final DemoInterceptor demoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registramos el interceptor para que evalúe todas las peticiones,
        // excluyendo rutas estáticas.
        registry.addInterceptor(demoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/fonts/**", "/assets/**");
    }
}
