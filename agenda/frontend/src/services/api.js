// URL base condicional: si estamos en desarrollo (Vite) apunta al puerto 8080,
// de lo contrario llama de forma relativa (servido por Spring Boot).
const BASE_URL = import.meta.env.DEV ? 'http://localhost:8080/api' : '/api';

/**
 * Helper centralizado para realizar peticiones HTTP de forma segura,
 * inyectando de forma automática el Bearer Token JWT si existe.
 */
async function request(endpoint, options = {}) {
    const token = localStorage.getItem('jwt_token');
    
    // Configuramos los encabezados básicos
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    // Si tenemos un token guardado en el navegador, lo adjuntamos en la cabecera Authorization
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, config);

        // Si la respuesta es 204 (No Content), no intentamos parsear JSON
        if (response.status === 204) {
            return null;
        }

        const data = await response.json();

        if (!response.ok) {
            // Si el backend devolvió un ErrorResponse estructurado, lanzamos su mensaje
            throw new Error(data.message || 'Ocurrió un error inesperado.');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Objeto de servicios expuestos para consumir la API
export const api = {
    // 1. AUTENTICACIÓN
    auth: {
        login: (username, password) => 
            request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            }),
        register: (username, email, password) =>
            request('/auth/register', {
                method: 'POST',
                body: JSON.stringify({ username, email, password })
            })
    },

    // 2. EVENTOS
    events: {
        getAll: () => request('/events'),
        getById: (id) => request(`/events/${id}`),
        create: (eventDto) => 
            request('/events', {
                method: 'POST',
                body: JSON.stringify(eventDto)
            }),
        update: (id, eventDto) =>
            request(`/events/${id}`, {
                method: 'PUT',
                body: JSON.stringify(eventDto)
            }),
        delete: (id) =>
            request(`/events/${id}`, {
                method: 'DELETE'
            })
    },

    // 3. TAREAS
    tasks: {
        getAll: () => request('/tasks'),
        create: (taskDto) =>
            request('/tasks', {
                method: 'POST',
                body: JSON.stringify(taskDto)
            }),
        update: (id, taskDto) =>
            request(`/tasks/${id}`, {
                method: 'PUT',
                body: JSON.stringify(taskDto)
            }),
        delete: (id) =>
            request(`/tasks/${id}`, {
                method: 'DELETE'
            })
    },

    // 4. NOTAS
    notes: {
        getAll: () => request('/notes'),
        create: (noteDto) =>
            request('/notes', {
                method: 'POST',
                body: JSON.stringify(noteDto)
            }),
        update: (id, noteDto) =>
            request(`/notes/${id}`, {
                method: 'PUT',
                body: JSON.stringify(noteDto)
            }),
        delete: (id) =>
            request(`/notes/${id}`, {
                method: 'DELETE'
            })
    },

    // 5. CONFIGURACIÓN
    config: {
        get: () => request('/config'),
        update: (configDto) =>
            request('/config', {
                method: 'PUT',
                body: JSON.stringify(configDto)
            })
    }
};
