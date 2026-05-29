package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.UserConfigDTO;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.model.UserConfig;
import com.agenda.interactiva.repository.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestionar las configuraciones y preferencias de usuario.
 */
@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UserConfigRepository userConfigRepository;

    /**
     * Obtiene la configuración de un usuario. Si no existe, la inicializa con valores por defecto.
     */
    @Transactional
    public UserConfig getOrInitConfig(User user) {
        return userConfigRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserConfig newConfig = UserConfig.builder()
                            .user(user)
                            .theme("DARK")
                            .currency("USD")
                            .emailNotificationsEnabled(true)
                            .build();
                    return userConfigRepository.save(newConfig);
                });
    }

    /**
     * Actualiza la configuración de preferencias de tema y alertas de correo.
     */
    @Transactional
    public UserConfig updateConfig(User user, UserConfigDTO dto) {
        UserConfig config = getOrInitConfig(user);
        
        if (dto.getTheme() != null) {
            config.setTheme(dto.getTheme());
        }
        if (dto.getCurrency() != null) {
            config.setCurrency(dto.getCurrency());
        }
        if (dto.getEmailNotificationsEnabled() != null) {
            config.setEmailNotificationsEnabled(dto.getEmailNotificationsEnabled());
        }
        
        return userConfigRepository.save(config);
    }
}
