import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus } from 'lucide-react';

/**
 * Página de Registro de Cuentas de Usuario.
 */
export default function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    
    const [formError, setFormError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    
    const { register, loading, error, setError } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setFormError('');
        setSuccessMsg('');
        if (setError) setError(null); // Limpiar errores globales previos

        // Validaciones en el cliente
        if (!username.trim() || !email.trim() || !password.trim() || !confirmPassword.trim()) {
            setFormError('Todos los campos son obligatorios.');
            return;
        }

        if (password.length < 6) {
            setFormError('La contraseña debe tener al menos 6 caracteres.');
            return;
        }

        if (password !== confirmPassword) {
            setFormError('Las contraseñas no coinciden.');
            return;
        }

        try {
            const success = await register(username, email, password);
            if (success) {
                setSuccessMsg('¡Registro completado! Redirigiendo al login...');
                setTimeout(() => {
                    navigate('/login');
                }, 2000);
            }
        } catch (err) {
            // Error capturado en el AuthContext y visualizado en el form
        }
    };

    return (
        <div className="auth-wrapper">
            <article className="glass-panel auth-card">
                <header className="auth-header">
                    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
                        <div style={{ background: 'rgba(102, 252, 241, 0.1)', padding: '1rem', borderRadius: '50%' }}>
                            <UserPlus size={40} color="var(--accent)" />
                        </div>
                    </div>
                    <h1>Crea tu cuenta</h1>
                    <p>Accede de forma gratuita a la agenda interactiva premium</p>
                </header>

                <form onSubmit={handleSubmit}>
                    {/* Banners de error y éxito */}
                    {formError && <div className="error-banner">{formError}</div>}
                    {error && <div className="error-banner">{error}</div>}
                    {successMsg && (
                        <div className="error-banner" style={{ background: 'rgba(16, 185, 129, 0.15)', borderColor: 'rgba(16, 185, 129, 0.3)', color: '#a7f3d0' }}>
                            {successMsg}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="username">Nombre de Usuario</label>
                        <input
                            type="text"
                            id="username"
                            className="form-control"
                            placeholder="Ej: esteban199"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Correo Electrónico</label>
                        <input
                            type="email"
                            id="email"
                            className="form-control"
                            placeholder="Ej: usuario@correo.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Contraseña (Mínimo 6 caracteres)</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            placeholder="Crea una contraseña segura"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirmar Contraseña</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            className="form-control"
                            placeholder="Repite la contraseña"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
                        {loading ? 'Registrando...' : 'Comenzar Ahora'}
                    </button>
                </form>

                <footer className="auth-footer">
                    ¿Ya tienes una cuenta? <Link to="/login" className="auth-link">Inicia sesión aquí</Link>
                </footer>
            </article>
        </div>
    );
}
