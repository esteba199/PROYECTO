package com.agenda.interactiva;

import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.UserRepository;
import com.agenda.interactiva.service.UserConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase que se ejecuta automáticamente al arrancar la aplicación.
 * Se encarga de inicializar datos por defecto en la base de datos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConfigService userConfigService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Buscar si ya existe el usuario administrador
        var optionalAdmin = userRepository.findByUsername("admin");
        
        if (optionalAdmin.isEmpty()) {
            log.info("Creando usuario administrador por defecto...");
            
            User admin = User.builder()
                    .username("admin")
                    .email("admin@agendainteractiva.com")
                    .password(passwordEncoder.encode("admin"))
                    .role("ROLE_ADMIN")
                    .build();
            
            User savedAdmin = userRepository.save(admin);
            userConfigService.getOrInitConfig(savedAdmin);
            
            log.info("¡Usuario administrador creado con éxito! (Usuario: admin / Contraseña: admin)");
        } else {
            log.info("El usuario administrador ya existe. Forzando actualización de la contraseña a 'admin' para asegurar el acceso...");
            User admin = optionalAdmin.get();
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
            log.info("¡Contraseña del administrador restablecida a 'admin'!");
        }

        // Crear usuario Demo si no existe
        var optionalDemo = userRepository.findByUsername("demo");
        if (optionalDemo.isEmpty()) {
            log.info("Creando usuario DEMO por defecto...");
            User demo = User.builder()
                    .username("demo")
                    .email("demo@agenda.com")
                    .password(passwordEncoder.encode("demo"))
                    .role("ROLE_GUEST")
                    .build();
            
            User savedDemo = userRepository.save(demo);
            userConfigService.getOrInitConfig(savedDemo);
            log.info("¡Usuario DEMO creado con éxito! (Usuario: demo / Contraseña: demo)");
        }
    }
}
