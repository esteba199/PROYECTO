import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
    Calendar as CalendarIcon, 
    CheckSquare, 
    FileText, 
    Clock, 
    AlertTriangle 
} from 'lucide-react';
import { Link } from 'react-router-dom';

/**
 * Página del Dashboard Principal (Notion / Apple Style).
 * Presenta un resumen interactivo de las métricas clave, próximos eventos y tareas prioritarias.
 */
export default function Dashboard() {
    const { user } = useAuth();
    const [events, setEvents] = useState([]);
    const [tasks, setTasks] = useState([]);
    const [notes, setNotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [eventsData, tasksData, notesData] = await Promise.all([
                    api.events.getAll(),
                    api.tasks.getAll(),
                    api.notes.getAll()
                ]);
                setEvents(eventsData || []);
                setTasks(tasksData || []);
                setNotes(notesData || []);
            } catch (err) {
                setError('Error al cargar la información del Dashboard.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    // Marcadores y métricas rápidos
    const pendingTasks = tasks.filter(t => !t.isCompleted);
    const highPriorityTasks = pendingTasks.filter(t => t.priority === 'HIGH');
    const upcomingEvents = events
        .filter(e => new Date(e.startTime) >= new Date())
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
        .slice(0, 3); // Solo los 3 siguientes eventos

    const toggleTaskComplete = async (task) => {
        try {
            const updated = await api.tasks.update(task.id, {
                ...task,
                isCompleted: !task.isCompleted
            });
            setTasks(tasks.map(t => t.id === task.id ? updated : t));
        } catch (err) {
            console.error('Error al actualizar tarea', err);
        }
    };

    if (loading) {
        return (
            <div className="loading-container" style={{ height: '50vh' }}>
                <div className="spinner"></div>
                <p>Cargando panel...</p>
            </div>
        );
    }

    return (
        <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
            <header style={{ marginBottom: '2.5rem' }}>
                <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '0.5rem' }}>
                    ¡Hola, {user ? user.username : 'Usuario'}! 👋
                </h1>
                <p style={{ color: 'var(--text-secondary)' }}>
                    Aquí tienes un resumen inteligente de tus recordatorios, eventos y tareas pendientes.
                </p>
            </header>

            {error && <div className="error-banner">{error}</div>}

            {/* GRILLA DE MÉTRICAS */}
            <section style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
                <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', padding: '1.5rem' }}>
                    <div style={{ background: 'rgba(102, 252, 241, 0.1)', padding: '0.75rem', borderRadius: '12px' }}>
                        <CalendarIcon size={24} color="var(--accent)" />
                    </div>
                    <div>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{events.length}</h2>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>Eventos programados</p>
                    </div>
                </div>

                <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', padding: '1.5rem' }}>
                    <div style={{ background: 'rgba(245, 158, 11, 0.1)', padding: '0.75rem', borderRadius: '12px' }}>
                        <CheckSquare size={24} color="#f59e0b" />
                    </div>
                    <div>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{pendingTasks.length}</h2>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>Tareas pendientes</p>
                    </div>
                </div>

                <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', padding: '1.5rem' }}>
                    <div style={{ background: 'rgba(59, 130, 246, 0.1)', padding: '0.75rem', borderRadius: '12px' }}>
                        <FileText size={24} color="#3b82f6" />
                    </div>
                    <div>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{notes.length}</h2>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>Notas guardadas</p>
                    </div>
                </div>
            </section>

            {/* SECCIÓN DIVIDIDA */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '2rem' }}>
                
                {/* COLUMNA IZQUIERDA: PRÓXIMOS EVENTOS */}
                <article className="glass-panel">
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Clock size={20} color="var(--accent)" />
                        Próximos Eventos
                    </h2>
                    
                    {upcomingEvents.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-secondary)' }}>
                            <p>No tienes eventos futuros programados.</p>
                            <Link to="/calendar" className="btn btn-secondary" style={{ marginTop: '1rem', padding: '0.5rem 1rem' }}>
                                Crear Evento
                            </Link>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {upcomingEvents.map(event => (
                                <div key={event.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--glass-border)', borderRadius: '12px' }}>
                                    <div>
                                        <h3 style={{ fontSize: '1rem', fontWeight: 600, color: event.color || 'var(--text-primary)' }}>
                                            {event.title}
                                        </h3>
                                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                                            {new Date(event.startTime).toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' })}
                                        </p>
                                    </div>
                                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                                        {event.location || 'Sin ubicación'}
                                    </span>
                                </div>
                            ))}
                        </div>
                    )}
                </article>

                {/* COLUMNA DERECHA: TAREAS URGENTES */}
                <article className="glass-panel">
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <AlertTriangle size={20} color="#ef4444" />
                        Tareas de Alta Prioridad
                    </h2>
                    
                    {highPriorityTasks.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-secondary)' }}>
                            <p>No tienes tareas de alta prioridad pendientes.</p>
                            <Link to="/tasks" className="btn btn-secondary" style={{ marginTop: '1rem', padding: '0.5rem 1rem' }}>
                                Ir a Tareas
                            </Link>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {highPriorityTasks.map(task => (
                                <div key={task.id} style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '1rem', background: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.15)', borderRadius: '12px' }}>
                                    <input 
                                        type="checkbox" 
                                        checked={task.isCompleted} 
                                        onChange={() => toggleTaskComplete(task)}
                                        style={{ cursor: 'pointer', width: '18px', height: '18px' }}
                                    />
                                    <div style={{ flex: 1 }}>
                                        <h3 style={{ fontSize: '0.95rem', fontWeight: 600 }}>{task.title}</h3>
                                        {task.dueDate && (
                                            <p style={{ fontSize: '0.8rem', color: '#fca5a5', marginTop: '0.25rem' }}>
                                                Vence: {new Date(task.dueDate).toLocaleDateString('es-ES')}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </article>
            </div>
        </div>
    );
}
