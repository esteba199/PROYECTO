import { createContext, useState, useEffect, useContext } from 'react';
import { api } from '../services/api';

const AuthContext = createContext(null);

/**
 * Proveedor de Contexto de Autenticación.
 * Guarda el token de sesión (JWT) y los datos básicos del usuario autenticado de forma global.
 * Esto permite proteger las rutas del frontend y recuperar el estado en cualquier componente.
 */
export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('jwt_token'));
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Al cargar la aplicación, comprobamos si ya hay un token guardado.
    useEffect(() => {
        const storedToken = localStorage.getItem('jwt_token');
        const storedUsername = localStorage.getItem('username');
        const storedRole = localStorage.getItem('role');

        if (storedToken && storedUsername) {
            setToken(storedToken);
            setUser({ username: storedUsername, role: storedRole });
        }
        setLoading(false);
    }, []);

    /**
     * Inicia sesión del usuario llamando a la API y guardando el Token JWT devuelto.
     */
    const login = async (username, password) => {
        setLoading(true);
        setError(null);
        try {
            const data = await api.auth.login(username, password);
            
            // Guardamos el token y datos en localStorage para persistir la sesión al recargar
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            
            setToken(data.token);
            setUser({ username: data.username, role: data.role });
            return true;
        } catch (err) {
            setError(err.message || 'Error al iniciar sesión.');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    /**
     * Registra un nuevo usuario en la base de datos del backend.
     */
    const register = async (username, email, password) => {
        setLoading(true);
        setError(null);
        try {
            await api.auth.register(username, email, password);
            return true;
        } catch (err) {
            setError(err.message || 'Error al registrar usuario.');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    /**
     * Cierra la sesión activa borrando el token y la información del almacenamiento local.
     */
    const logout = () => {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        setToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, token, loading, error, login, register, logout, setError }}>
            {children}
        </AuthContext.Provider>
    );
}

/**
 * Hook personalizado para consumir de forma abreviada el contexto de autenticación.
 */
export function useAuth() {
    return useContext(AuthContext);
}
