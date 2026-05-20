import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Componente Wrapper para Proteger Rutas del Frontend.
 * 
 * Si el usuario no está autenticado, lo redirige automáticamente a la página de Login.
 * Opcionalmente, permite restringir el acceso a roles específicos (como ROLE_ADMIN).
 */
export function ProtectedRoute({ children, allowedRoles }) {
    const { user, token, loading } = useAuth();

    // Mientras se verifica el estado de autenticación inicial, mostramos una pantalla de carga premium.
    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Verificando credenciales...</p>
            </div>
        );
    }

    // Si no hay token de sesión, redirigimos directamente a /login
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // Si se especificaron roles permitidos y el usuario no cuenta con ninguno, denegamos el acceso.
    if (allowedRoles && user && !allowedRoles.includes(user.role)) {
        return <Navigate to="/dashboard" replace />;
    }

    // Si pasa todas las validaciones, renderiza la página solicitada (children)
    return children;
}
