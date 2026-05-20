import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Trash2, CheckCircle2, Circle, AlertCircle, Edit2, Calendar } from 'lucide-react';

/**
 * Página de Tareas (To-Do List).
 * Ofrece filtros por estado, ordenamiento por prioridad y creación interactiva.
 */
export default function TasksPage() {
    const [tasks, setTasks] = useState([]);
    const [filter, setFilter] = useState('ALL'); // 'ALL', 'PENDING', 'COMPLETED'
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estados de Formulario de Creación/Edición
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingTask, setEditingTask] = useState(null);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [priority, setPriority] = useState('MEDIUM');
    const [dueDate, setDueDate] = useState('');

    useEffect(() => {
        fetchTasks();
    }, []);

    const fetchTasks = async () => {
        try {
            const data = await api.tasks.getAll();
            setTasks(data || []);
        } catch (err) {
            setError('Error al cargar la lista de tareas.');
        } finally {
            setLoading(false);
        }
    };

    const handleSaveTask = async (e) => {
        e.preventDefault();
        if (!title.trim()) return;

        const taskDto = {
            title,
            description,
            priority,
            dueDate: dueDate ? new Date(dueDate).toISOString() : null
        };

        try {
            if (editingTask) {
                const updated = await api.tasks.update(editingTask.id, {
                    ...editingTask,
                    ...taskDto
                });
                setTasks(tasks.map(t => t.id === editingTask.id ? updated : t));
            } else {
                const created = await api.tasks.create(taskDto);
                setTasks([...tasks, created]);
            }
            resetForm();
        } catch (err) {
            alert('Error al guardar la tarea.');
        }
    };

    const toggleComplete = async (task) => {
        try {
            const updated = await api.tasks.update(task.id, {
                ...task,
                isCompleted: !task.isCompleted
            });
            setTasks(tasks.map(t => t.id === task.id ? updated : t));
        } catch (err) {
            console.error(err);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Seguro que deseas eliminar esta tarea?')) {
            try {
                await api.tasks.delete(id);
                setTasks(tasks.filter(t => t.id !== id));
            } catch (err) {
                alert('No se pudo borrar la tarea.');
            }
        }
    };

    const openEditForm = (task) => {
        setEditingTask(task);
        setTitle(task.title);
        setDescription(task.description || '');
        setPriority(task.priority || 'MEDIUM');
        setDueDate(task.dueDate ? task.dueDate.slice(0, 16) : '');
        setIsFormOpen(true);
    };

    const resetForm = () => {
        setIsFormOpen(false);
        setEditingTask(null);
        setTitle('');
        setDescription('');
        setPriority('MEDIUM');
        setDueDate('');
    };

    // Filtrar tareas según el tab seleccionado
    const filteredTasks = tasks.filter(task => {
        if (filter === 'PENDING') return !task.isCompleted;
        if (filter === 'COMPLETED') return task.isCompleted;
        return true;
    });

    // Mapeador visual de prioridades
    const priorityLabels = {
        HIGH: { label: 'Alta', color: '#ef4444', bg: 'rgba(239, 68, 68, 0.15)' },
        MEDIUM: { label: 'Media', color: '#f59e0b', bg: 'rgba(245, 158, 11, 0.15)' },
        LOW: { label: 'Baja', color: '#10b981', bg: 'rgba(16, 185, 129, 0.15)' }
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Cargando tareas...</p>
            </div>
        );
    }

    return (
        <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
            {error && <div className="error-banner">{error}</div>}

            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <div>
                    <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Gestión de Tareas</h1>
                    <p style={{ color: 'var(--text-secondary)' }}>Organiza tus pendientes diarios por prioridad y fecha límite.</p>
                </div>
                
                <button onClick={() => setIsFormOpen(true)} className="btn btn-primary">
                    <Plus size={18} />
                    Agregar Tarea
                </button>
            </header>

            {/* BARRA DE FILTROS */}
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--glass-border)', paddingBottom: '0.75rem' }}>
                {['ALL', 'PENDING', 'COMPLETED'].map(f => (
                    <button 
                        key={f}
                        onClick={() => setFilter(f)}
                        className="btn"
                        style={{ 
                            background: filter === f ? 'rgba(102, 252, 241, 0.1)' : 'transparent',
                            color: filter === f ? 'var(--accent)' : 'var(--text-secondary)',
                            border: 'none',
                            padding: '0.5rem 1rem',
                            fontSize: '0.9rem'
                        }}
                    >
                        {f === 'ALL' ? 'Todas' : f === 'PENDING' ? 'Pendientes' : 'Completadas'}
                    </button>
                ))}
            </div>

            {/* FORMULARIO DE TAREA INLINE (O CONDICIONAL) */}
            {isFormOpen && (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem' }}>
                    <div className="glass-panel" style={{ width: '100%', maxWidth: '500px', animation: 'fadeIn 0.3s ease-out' }}>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '1.5rem' }}>
                            {editingTask ? 'Editar Tarea' : 'Nueva Tarea'}
                        </h2>
                        
                        <form onSubmit={handleSaveTask} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div className="form-group">
                                <label>Título de la Tarea</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    required 
                                    value={title} 
                                    onChange={(e) => setTitle(e.target.value)} 
                                    placeholder="Ej: Terminar informe de ventas..."
                                />
                            </div>

                            <div className="form-group">
                                <label>Descripción</label>
                                <textarea 
                                    className="form-control" 
                                    value={description} 
                                    onChange={(e) => setDescription(e.target.value)} 
                                    placeholder="Detalles adicionales..."
                                    rows="3"
                                />
                            </div>

                            <div className="form-group">
                                <label>Prioridad</label>
                                <select 
                                    className="form-control" 
                                    value={priority} 
                                    onChange={(e) => setPriority(e.target.value)}
                                >
                                    <option value="LOW">Baja</option>
                                    <option value="MEDIUM">Media</option>
                                    <option value="HIGH">Alta</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label><Calendar size={14} /> Fecha Límite</label>
                                <input 
                                    type="datetime-local" 
                                    className="form-control" 
                                    value={dueDate} 
                                    onChange={(e) => setDueDate(e.target.value)} 
                                />
                            </div>

                            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                                    Guardar
                                </button>
                                <button type="button" onClick={resetForm} className="btn btn-secondary" style={{ flex: 1 }}>
                                    Cancelar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* LISTADO DE TAREAS */}
            {filteredTasks.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                    <AlertCircle size={40} style={{ marginBottom: '1rem', color: 'var(--text-secondary)' }} />
                    <p>No se encontraron tareas en esta sección.</p>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    {filteredTasks.map(task => {
                        const prio = priorityLabels[task.priority] || priorityLabels.MEDIUM;
                        return (
                            <article 
                                key={task.id} 
                                className="glass-panel" 
                                style={{ 
                                    display: 'flex', 
                                    alignItems: 'center', 
                                    gap: '1rem', 
                                    padding: '1.25rem',
                                    opacity: task.isCompleted ? 0.6 : 1,
                                    textDecoration: task.isCompleted ? 'line-through' : 'none'
                                }}
                            >
                                <button 
                                    onClick={() => toggleComplete(task)}
                                    style={{ background: 'none', border: 'none', cursor: 'pointer', color: task.isCompleted ? 'var(--accent)' : 'var(--text-secondary)' }}
                                >
                                    {task.isCompleted ? <CheckCircle2 size={22} /> : <Circle size={22} />}
                                </button>

                                <div style={{ flex: 1 }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
                                        <h3 style={{ fontSize: '1.05rem', fontWeight: 600 }}>{task.title}</h3>
                                        <span style={{ fontSize: '0.75rem', fontWeight: 600, padding: '2px 8px', borderRadius: '4px', background: prio.bg, color: prio.color }}>
                                            {prio.label}
                                        </span>
                                    </div>
                                    {task.description && (
                                        <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                                            {task.description}
                                        </p>
                                    )}
                                    {task.dueDate && (
                                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.25rem', marginTop: '0.4rem' }}>
                                            <Calendar size={12} />
                                            Límite: {new Date(task.dueDate).toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' })}
                                        </p>
                                    )}
                                </div>

                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <button onClick={() => openEditForm(task)} className="btn btn-secondary" style={{ padding: '0.5rem' }}>
                                        <Edit2 size={16} />
                                    </button>
                                    <button onClick={() => handleDelete(task.id)} className="btn btn-secondary" style={{ padding: '0.5rem', color: '#ef4444', borderColor: 'rgba(239, 68, 68, 0.1)' }}>
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </article>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
