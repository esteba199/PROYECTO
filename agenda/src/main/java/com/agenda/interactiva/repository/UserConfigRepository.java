package com.agenda.interactiva.repository;

import com.agenda.interactiva.model.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad UserConfig.
 */
@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
    
    // Busca la configuración asociada a un usuario específico.
    Optional<UserConfig> findByUserId(Long userId);
}
