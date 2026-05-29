/**
 * Lógica Frontend Global - Agenda Interactiva
 * ============================================
 * NOTA IMPORTANTE: El tema se aplica en <head> via script síncrono inline
 * (en plantilla_base.html) para evitar el flash blanco/negro.
 * Este archivo gestiona el resto:
 *
 * 1. Sincronizar body.dark-mode con html.dark-mode.
 * 2. Resaltar ítem activo del menú.
 * 3. Cargar preferencias desde MySQL (/api/config) — sin sobrescribir el tema visible.
 * 4. Sistema global de Toast Notifications.
 * 5. Toggle de tema Oscuro/Claro.
 * 6. Modal global de acceso restringido para modo invitado.
 */

// ─── SINCRONIZACIÓN INMEDIATA DEL BODY ────────────────────────────────────────
// El script del <head> ya puso la clase en <html>. Aquí aseguramos que <body> coincida.
(function syncBodyTheme() {
    if (document.documentElement.classList.contains('dark-mode')) {
        document.body.classList.add('dark-mode');
    } else {
        document.body.classList.remove('dark-mode');
    }
})();

// ─── CUANDO EL DOM ESTÁ LISTO ─────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {

    // 1. RESALTAR ÍTEM ACTIVO DEL MENÚ
    const currentPath = window.location.pathname;
    const menuMap = {
        '/panel':      'menu-panel',
        '/calendario': 'menu-calendario',
        '/tareas':     'menu-tareas',
        '/notas':      'menu-notas',
        '/finanzas':   'menu-finanzas',
        '/foco':       'menu-foco',
        '/perfil':     'menu-perfil',
    };
    Object.entries(menuMap).forEach(([path, id]) => {
        if (currentPath.includes(path)) {
            document.getElementById(id)?.classList.add('active');
        }
    });

    // 2. Sincronizar el ícono del tema según el estado actual
    updateThemeIcon();

    // 3. CARGAR TEMA DESDE MYSQL (solo para sincronizar, sin flash)
    fetch('/api/config')
        .then(res => {
            if (!res.ok) throw new Error('No autenticado');
            return res.json();
        })
        .then(config => {
            const serverTheme = config.theme || 'DARK';
            const localTheme = localStorage.getItem('agenda-theme') || 'DARK';

            // Solo actualizar si el servidor tiene un tema diferente al local
            // Esto evita flashes innecesarios al navegar entre secciones
            if (serverTheme !== localTheme) {
                localStorage.setItem('agenda-theme', serverTheme);
                applyTheme(serverTheme);
            }
        })
        .catch(() => {
            // Vista pública (login, registro) — no hacer nada
        });

    // 4. ESCUCHAR BOTÓN DE TOGGLE DE TEMA (si está en esta página)
    document.getElementById('theme-toggle-btn')?.addEventListener('click', toggleTheme);
});

// ─── FUNCIONES GLOBALES ───────────────────────────────────────────────────────

/**
 * Aplica el tema visualmente al <html> y <body> y actualiza el ícono del toggle.
 * @param {'DARK'|'LIGHT'} theme
 */
function applyTheme(theme) {
    if (theme === 'DARK') {
        document.documentElement.classList.add('dark-mode');
        document.body.classList.add('dark-mode');
    } else {
        document.documentElement.classList.remove('dark-mode');
        document.body.classList.remove('dark-mode');
    }
    updateThemeIcon();
}

/**
 * Actualiza el ícono del botón de toggle de tema según el estado actual.
 */
function updateThemeIcon() {
    const icon = document.getElementById('theme-toggle-icon');
    if (!icon) return;
    const isDark = document.documentElement.classList.contains('dark-mode');
    icon.className = isDark ? 'ph ph-sun' : 'ph ph-moon';
}

/**
 * Alterna entre Modo Oscuro y Claro, y persiste en MySQL.
 */
async function toggleTheme() {
    const isDark = document.documentElement.classList.contains('dark-mode');
    const newTheme = isDark ? 'LIGHT' : 'DARK';

    // Aplicar visualmente de inmediato
    applyTheme(newTheme);
    localStorage.setItem('agenda-theme', newTheme);

    // Persistir en base de datos
    try {
        const res = await fetch('/api/config', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ theme: newTheme })
        });
        if (res.status === 403) {
            showAccessRestrictedModal();
            return;
        }
        if (!res.ok) throw new Error('Error al guardar');
    } catch (e) {
        showToast('No se pudo guardar la preferencia de tema', 'error');
    }
}

// ─── SISTEMA GLOBAL DE TOAST NOTIFICATIONS ────────────────────────────────────

/**
 * Muestra una notificación Toast emergente.
 * @param {string} message
 * @param {'success'|'error'|'info'|'warning'} type
 */
function showToast(message, type = 'info') {
    let container = document.getElementById('global-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'global-toast-container';
        container.style.cssText =
            'position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:10px;pointer-events:none;';
        document.body.appendChild(container);
    }

    const styles = {
        success: { icon: 'ph-check-circle',  bg: '#2ecc71', border: '#27ae60' },
        error:   { icon: 'ph-x-circle',      bg: '#e74c3c', border: '#c0392b' },
        info:    { icon: 'ph-info',           bg: '#3498db', border: '#2980b9' },
        warning: { icon: 'ph-warning-circle', bg: '#f39c12', border: '#d68910' },
    };
    const s = styles[type] || styles.info;

    const toast = document.createElement('div');
    toast.style.cssText = `
        background:${s.bg}; border-left:4px solid ${s.border}; color:#fff;
        padding:14px 20px; border-radius:10px; min-width:260px; max-width:380px;
        display:flex; align-items:center; gap:12px; font-weight:500;
        box-shadow:0 10px 30px rgba(0,0,0,0.25);
        font-family:'Inter',sans-serif; font-size:0.95rem;
        transform:translateX(120%);
        transition:transform 0.35s cubic-bezier(0.68,-0.55,0.265,1.55);
        pointer-events:auto;
    `;
    toast.innerHTML = `<i class="ph ${s.icon}" style="font-size:1.4rem;flex-shrink:0;"></i><span>${message}</span>`;
    container.appendChild(toast);

    // Entrada
    requestAnimationFrame(() => requestAnimationFrame(() => {
        toast.style.transform = 'translateX(0)';
    }));

    // Salida automática tras 4 s
    setTimeout(() => {
        toast.style.transform = 'translateX(120%)';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// ─── MODAL GLOBAL DE ACCESO RESTRINGIDO ───────────────────────────────────────

/**
 * Muestra un modal premium indicando que el usuario invitado
 * no tiene acceso a esta funcionalidad.
 */
function showAccessRestrictedModal() {
    // Si ya existe el modal, simplemente mostrarlo
    let modal = document.getElementById('accessRestrictedModal');
    if (modal) {
        modal.classList.add('active');
        return;
    }

    modal = document.createElement('div');
    modal.id = 'accessRestrictedModal';
    modal.className = 'access-modal-overlay active';
    modal.innerHTML = `
        <div class="access-modal-card">
            <div class="access-modal-icon">
                <i class="ph ph-lock-key"></i>
            </div>
            <h2>Acceso Restringido</h2>
            <p>Estás en <strong>Modo Invitado</strong>. Esta funcionalidad solo está disponible para usuarios registrados.</p>
            <div class="access-modal-features">
                <div class="access-feature"><i class="ph ph-check-circle"></i> Guardar datos personalizados</div>
                <div class="access-feature"><i class="ph ph-check-circle"></i> Modo Foco con estadísticas</div>
                <div class="access-feature"><i class="ph ph-check-circle"></i> Gestión financiera completa</div>
                <div class="access-feature"><i class="ph ph-check-circle"></i> Configuración de perfil</div>
            </div>
            <div class="access-modal-actions">
                <a href="/registro" class="btn btn-primary" style="flex:1;text-align:center;">
                    <i class="ph ph-user-plus"></i> Registrarse Gratis
                </a>
                <button class="btn btn-outline" onclick="closeAccessModal()" style="flex:1;">
                    Seguir Explorando
                </button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);

    // Cerrar al clicar fuera
    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeAccessModal();
    });
}

function closeAccessModal() {
    const modal = document.getElementById('accessRestrictedModal');
    if (modal) modal.classList.remove('active');
}

/**
 * Helper global para manejar respuestas 403 en llamadas fetch del modo invitado.
 * Devuelve true si la respuesta fue 403 (y muestra el modal), false si no.
 * @param {Response} response - Respuesta del fetch
 * @returns {boolean}
 */
function handleDemoRestriction(response) {
    if (response.status === 403) {
        showAccessRestrictedModal();
        return true;
    }
    return false;
}
