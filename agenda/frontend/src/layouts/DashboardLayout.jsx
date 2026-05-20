import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
    LayoutDashboard, 
    Calendar as CalendarIcon, 
    CheckSquare, 
    FileText, 
    User as UserIcon, 
    LogOut,
    Menu
} from 'lucide-react';

/**
 * Layout Principal del Dashboard.
 * 
 * Contiene una barra de navegación lateral (Sidebar) con estilo Glassmorphic
 * y una barra superior (Top Navbar). Renderiza la ruta activa mediante <Outlet />.
 */
export default function DashboardLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="dashboard-layout">
            {/* Blobs de fondo */}
            <div className="bg-blobs">
                <div className="blob blob-1"></div>
                <div className="blob blob-2"></div>
            </div>

            {/* BARRA LATERAL (SIDEBAR) */}
            <aside className="sidebar">
                <div className="sidebar-brand">
                    <CalendarIcon color="var(--accent)" size={24} />
                    <span>Agenda Inteligente</span>
                </div>

                <nav className="sidebar-nav">
                    <NavLink to="/dashboard" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                        <LayoutDashboard size={18} />
                        Dashboard
                    </NavLink>
                    <NavLink to="/calendar" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                        <CalendarIcon size={18} />
                        Calendario
                    </NavLink>
                    <NavLink to="/tasks" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                        <CheckSquare size={18} />
                        Tareas
                    </NavLink>
                    <NavLink to="/notes" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                        <FileText size={18} />
                        Notas Rápidas
                    </NavLink>
                    <NavLink to="/profile" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                        <UserIcon size={18} />
                        Mi Perfil
                    </NavLink>
                </nav>

                <div className="sidebar-footer">
                    <button 
                        onClick={handleLogout} 
                        className="sidebar-link" 
                        style={{ background: 'none', border: 'none', width: '100%', cursor: 'pointer', textAlign: 'left' }}
                    >
                        <LogOut size={18} />
                        Cerrar Sesión
                    </button>
                </div>
            </aside>

            {/* CONTENIDO PRINCIPAL */}
            <div className="main-content">
                <header className="top-navbar">
                    <div>
                        {/* Se puede añadir un título dinámico de la página */}
                    </div>
                    <div className="user-badge">
                        <UserIcon size={16} color="var(--accent)" />
                        <span style={{ fontSize: '0.9rem', fontWeight: 500 }}>
                            {user ? user.username : 'Usuario'}
                        </span>
                    </div>
                </header>

                <main className="page-container">
                    {/* Renderiza los componentes hijos asignados por React Router */}
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
