import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Calendar } from 'lucide-react';

/**
 * Página de Inicio de Sesión (Login).
 * Cuenta con un diseño Premium Glassmorphic y validaciones de formulario.
 */
export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [formError, setFormError] = useState('');
    const { login, loading, error } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setFormError('');

        // Validaciones en Frontend antes de enviar datos al servidor
        if (!username.trim() || !password.trim()) {
            setFormError('Por favor, rellene todos los campos.');
            return;
        }

        try {
            const success = await login(username, password);
            if (success) {
                navigate('/dashboard'); // Redirigir al Dashboard principal
            }
        } catch (err) {
            // El error es manejado por el AuthContext
        }
    };

    return (
        <div className="auth-wrapper">
            <article className="glass-panel auth-card">
                <header className="auth-header">
                    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
                        <div style={{ background: 'rgba(102, 252, 241, 0.1)', padding: '1rem', borderRadius: '50%' }}>
                            <Calendar size={40} color="var(--accent)" />
                        </div>
                    </div>
                    <h1>Agenda Inteligente</h1>
                    <p>Inicia sesión para gestionar tus actividades cotidianas</p>
                </header>

                <form onSubmit={handleSubmit}>
                    {/* Banners de errores */}
                    {formError && <div className="error-banner">{formError}</div>}
                    {error && <div className="error-banner">{error}</div>}

                    <div className="form-group">
                        <label htmlFor="username">Usuario</label>
                        <input
                            type="text"
                            id="username"
                            className="form-control"
                            placeholder="Introduce tu usuario"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Contraseña</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            placeholder="Introduce tu contraseña"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
                        {loading ? 'Iniciando sesión...' : 'Entrar'}
                    </button>
                </form>

                <footer className="auth-footer">
                    ¿No tienes cuenta? <Link to="/register" className="auth-link">Regístrate gratis</Link>
                </footer>
            </article>
        </div>
    );
}
