-- ==========================================
-- SCRIPT DE BASE DE DATOS: AGENDA INTERACTIVA
-- ==========================================
-- Este archivo contiene las definiciones de tabla para MySQL.
-- Cada tabla está pensada para soportar una arquitectura profesional.
-- Se incluyen: timestamps (created_at, updated_at) y soft delete (deleted_at).

-- Usamos esta base de datos si no existe, o simplemente creamos las tablas.
-- Asegúrate de crear la base de datos "agenda_inteligente" en tu servidor MySQL:
-- CREATE DATABASE IF NOT EXISTS agenda_inteligente CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE agenda_inteligente;

-- 1. TABLA DE USUARIOS
-- Guarda la información principal de los usuarios para la autenticación y control de accesos.
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- El nombre de usuario debe ser único y no nulo.
    username VARCHAR(50) NOT NULL UNIQUE,
    
    -- La contraseña almacenará un hash bcrypt (60 caracteres), por seguridad nunca guardamos contraseñas en texto plano.
    password VARCHAR(255) NOT NULL,
    
    -- El email es único para evitar registros duplicados.
    email VARCHAR(100) NOT NULL UNIQUE,
    
    -- Rol del usuario en el sistema. Valores estándar: 'ROLE_USER', 'ROLE_ADMIN'.
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    
    -- Timestamps: para saber cuándo se creó y actualizó el registro automáticamente.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Soft Delete: si deleted_at no es nulo, significa que el usuario ha sido borrado del sistema (borrado lógico).
    deleted_at TIMESTAMP NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. TABLA DE CONFIGURACIONES DE USUARIO
-- Relación 1-a-1 con Usuarios. Permite guardar las preferencias del usuario.
CREATE TABLE IF NOT EXISTS configuraciones_usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Relación uno a uno. UNIQUE garantiza que un usuario solo tenga una configuración.
    usuario_id BIGINT NOT NULL UNIQUE,
    
    -- Preferencia de tema: 'LIGHT' o 'DARK' (por defecto oscuro).
    theme VARCHAR(20) DEFAULT 'DARK',
    
    -- Opción para desactivar el envío de correos recordatorios.
    email_notifications_enabled BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clave foránea que apunta al usuario. Si el usuario se elimina físicamente, se eliminan sus configuraciones.
    CONSTRAINT fk_configuracion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. TABLA DE EVENTOS
-- Representa las citas, reuniones o eventos programados por el usuario.
CREATE TABLE IF NOT EXISTS eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    
    title VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- Horarios de inicio y fin del evento.
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    
    location VARCHAR(255),
    
    -- Código hexadecimal de color para pintar el evento de forma interactiva en la interfaz.
    color VARCHAR(20) DEFAULT '#66fcf1',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    
    -- Relación Many-to-One: Muchos eventos pertenecen a un usuario.
    CONSTRAINT fk_evento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. TABLA DE RECORDATORIOS
-- Programaciones de alertas para eventos. Se relaciona con eventos.
CREATE TABLE IF NOT EXISTS recordatorios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    
    -- Fecha y hora en la que se debe notificar.
    notification_time DATETIME NOT NULL,
    
    -- Estado del recordatorio. Si ya fue enviado por el backend o no.
    is_sent BOOLEAN DEFAULT FALSE,
    
    -- Tipo de notificación: 'EMAIL' o 'POPUP'.
    type VARCHAR(20) DEFAULT 'EMAIL',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Relación: Muchos recordatorios pertenecen a un evento.
    CONSTRAINT fk_recordatorio_evento FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. TABLA DE NOTAS
-- Notas rápidas tipo post-it creadas por el usuario.
CREATE TABLE IF NOT EXISTS notas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    
    title VARCHAR(100) NOT NULL,
    content TEXT,
    
    -- Color para personalizar el post-it de la nota.
    color VARCHAR(20) DEFAULT '#1f2833',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    
    CONSTRAINT fk_nota_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. TABLA DE TAREAS
-- Tareas pendientes, con estados y prioridades.
CREATE TABLE IF NOT EXISTS tareas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    
    title VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- Fecha límite para completar la tarea.
    due_date DATETIME DEFAULT NULL,
    
    -- Indica si la tarea está completada.
    is_completed BOOLEAN DEFAULT FALSE,
    
    -- Prioridad de la tarea: 'LOW', 'MEDIUM', 'HIGH'.
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    
    CONSTRAINT fk_tarea_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
