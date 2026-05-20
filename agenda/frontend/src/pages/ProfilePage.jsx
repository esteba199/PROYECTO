import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { User, Shield, Bell, Moon, Sun, Settings } from 'lucide-react';

/**
 * Página de Perfil y Ajustes.
 * Permite cambiar preferencias de tema (modo oscuro/claro) y activar alertas de recordatorio por correo.
 */
export default function ProfilePage() {
    const { user } = useAuth();
    const [config, setConfig] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    useEffect(() => {
        fetchConfig();
    }, []);

    const fetchConfig = async () => {
        try {
            const data = await api.config.get();
            setConfig(data);
            
            // Aplicamos el tema recuperado de la BD en el DOM
            if (data && data.theme) {
                document.documentElement.setAttribute('data-theme', data.theme.toLowerCase());
            }
        } catch (err) {
            setError('Error al recuperar las configuraciones de usuario.');
        } finally {
            setLoading(false);
        }
    };

    const handleThemeChange = async (newTheme) => {
        try {
            setSuccessMsg('');
            const updated = await api.config.update({
                theme: newTheme
            });
            setConfig(updated);
            
            // Modificamos el DOM
            document.documentElement.setAttribute('data-theme', newTheme.toLowerCase());
            setSuccessMsg('Tema actualizado.');
        } catch (err) {
            setError('No se pudo guardar la preferencia de tema.');
        }
    };

    const handleNotificationToggle = async (enabled) => {
        try {
            setSuccessMsg('');
            const updated = await api.config.update({
                emailNotificationsEnabled: enabled
            });
            setConfig(updated);
            setSuccessMsg('Preferencia de correo guardada.');
        } catch (err) {
            setError('No se pudo actualizar el estado de notificaciones.');
        }
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Cargando perfil...</p>
            </div>
        );
    }

    return (
        <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
            {error && <div className="error-banner">{error}</div>}
            {successMsg && (
                <div className="error-banner" style={{ background: 'rgba(16, 185, 129, 0.15)', borderColor: 'rgba(16, 185, 129, 0.3)', color: '#a7f3d0' }}>
                    {successMsg}
                </div>
            )}

            <header style={{ marginBottom: '2.5rem' }}>
                <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Ajustes de Perfil</h1>
                <p style={{ color: 'var(--text-secondary)' }}>Configura tus preferencias visuales y alertas del sistema.</p>
            </header>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '2rem' }}>
                {/* TARJETA DE PERFIL (DATOS DE LA CUENTA) */}
                <article className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <User size={20} color="var(--accent)" />
                        Datos de la Cuenta
                    </h2>
                    
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--glass-border)', paddingBottom: '0.75rem' }}>
                            <span style={{ color: 'var(--text-secondary)' }}>Nombre de Usuario</span>
                            <span style={{ fontWeight: 600 }}>{user ? user.username : 'N/A'}</span>
                        </div>
                        
                        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--glass-border)', paddingBottom: '0.75rem' }}>
                            <span style={{ color: 'var(--text-secondary)' }}>Rol Asignado</span>
                            <span style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                                <Shield size={14} color="var(--accent)" />
                                {user ? user.role.replace('ROLE_', '') : 'USER'}
                            </span>
                        </div>
                    </div>
                </article>

                {/* TARJETA DE PREFERENCIAS (INTERFAZ Y ALERTAS) */}
                <article className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Settings size={20} color="var(--accent)" />
                        Preferencias
                    </h2>

                    {/* Tema */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--glass-border)', paddingBottom: '1rem' }}>
                        <div>
                            <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Apariencia de la Interfaz</h3>
                            <p style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', marginTop: '0.25rem' }}>Elige el estilo visual de tu agenda.</p>
                        </div>
                        <div style={{ display: 'flex', background: 'rgba(255,255,255,0.05)', padding: '4px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                            <button 
                                onClick={() => handleThemeChange('LIGHT')}
                                style={{ 
                                    background: config && config.theme === 'LIGHT' ? 'var(--accent)' : 'none',
                                    color: config && config.theme === 'LIGHT' ? 'black' : 'var(--text-secondary)',
                                    border: 'none', cursor: 'pointer', padding: '6px 12px', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.25rem' 
                                }}
                            >
                                <Sun size={14} />
                                Claro
                            </button>
                            <button 
                                onClick={() => handleThemeChange('DARK')}
                                style={{ 
                                    background: config && config.theme === 'DARK' ? 'var(--accent)' : 'none',
                                    color: config && config.theme === 'DARK' ? 'black' : 'var(--text-secondary)',
                                    border: 'none', cursor: 'pointer', padding: '6px 12px', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.25rem' 
                                }}
                            >
                                <Moon size={14} />
                                Oscuro
                            </button>
                        </div>
                    </div>

                    {/* Alertas */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingBottom: '1rem' }}>
                        <div>
                            <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Alertas por Correo</h3>
                            <p style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', marginTop: '0.25rem' }}>Recibir notificaciones por correo sobre próximos eventos.</p>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                            <input 
                                type="checkbox"
                                checked={config ? config.emailNotificationsEnabled : false}
                                onChange={(e) => handleNotificationToggle(e.target.checked)}
                                style={{ width: '22px', height: '22px', cursor: 'pointer' }}
                            />
                        </div>
                    </div>
                </article>
            </div>
        </div>
    );
}
