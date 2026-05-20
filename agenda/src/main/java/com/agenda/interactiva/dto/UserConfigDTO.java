package com.agenda.interactiva.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la transferencia de información de la Configuración del Usuario.
 */
@Getter
@Setter
public class UserConfigDTO {
    
    private String theme; // 'LIGHT' o 'DARK'
    private Boolean emailNotificationsEnabled;
}
