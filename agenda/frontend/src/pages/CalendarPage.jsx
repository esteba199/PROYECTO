import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { 
    ChevronLeft, 
    ChevronRight, 
    Plus, 
    MapPin, 
    Clock, 
    Trash2, 
    X,
    Bell
} from 'lucide-react';

/**
 * Vista de Calendario Mensual Interactivo.
 * Implementado desde cero para garantizar un diseño premium personalizado y glassmorphism puro.
 * Permite listar eventos, programar recordatorios, editarlos y borrarlos.
 */
export default function CalendarPage() {
    const [currentDate, setCurrentDate] = useState(new Date());
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estados de Modales
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [selectedDateStr, setSelectedDateStr] = useState('');

    // Datos del Formulario
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [location, setLocation] = useState('');
    const [color, setColor] = useState('#66fcf1');
    const [reminderMinutesBefore, setReminderMinutesBefore] = useState('15'); // 15 min por defecto
    const [enableReminder, setEnableReminder] = useState(false);

    useEffect(() => {
        fetchEvents();
    }, []);

    const fetchEvents = async () => {
        try {
            const data = await api.events.getAll();
            setEvents(data || []);
        } catch (err) {
            setError('Error al recuperar eventos de la base de datos.');
        } finally {
            setLoading(false);
        }
    };

    // Funciones de control de fecha
    const prevMonth = () => {
        setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
    };

    const nextMonth = () => {
        setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
    };

    // Generar cuadrícula de días para el mes activo
    const getDaysInMonth = () => {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        const firstDayIndex = new Date(year, month, 1).getDay(); // Día de la semana en que inicia
        const totalDays = new Date(year, month + 1, 0).getDate(); // Total de días del mes

        const days = [];

        // Celdas vacías para acomodar el inicio del mes en la cuadrícula
        const prevMonthTotalDays = new Date(year, month, 0).getDate();
        for (let i = firstDayIndex - 1; i >= 0; i--) {
            days.push({
                dayNum: prevMonthTotalDays - i,
                isCurrentMonth: false,
                date: new Date(year, month - 1, prevMonthTotalDays - i)
            });
        }

        // Celdas del mes actual
        for (let i = 1; i <= totalDays; i++) {
            days.push({
                dayNum: i,
                isCurrentMonth: true,
                date: new Date(year, month, i)
            });
        }

        return days;
    };

    // Abre el modal para crear un evento en una fecha específica
    const handleDayClick = (dayDate) => {
        setSelectedEvent(null);
        setSelectedDateStr(dayDate.toDateString());
        
        // Inicializar formulario con valores por defecto
        setTitle('');
        setDescription('');
        
        // Formatear fecha para el input datetime-local
        const datePart = dayDate.toISOString().split('T')[0];
        setStartTime(`${datePart}T09:00`);
        setEndTime(`${datePart}T10:00`);
        setLocation('');
        setColor('#66fcf1');
        setEnableReminder(false);
        
        setIsModalOpen(true);
    };

    // Abre el modal para visualizar/editar un evento existente
    const handleEventClick = (e, event) => {
        e.stopPropagation(); // Evita disparar el click del día
        setSelectedEvent(event);
        
        setTitle(event.title);
        setDescription(event.description || '');
        setStartTime(event.startTime.slice(0, 16)); // Truncar a formato input
        setEndTime(event.endTime.slice(0, 16));
        setLocation(event.location || '');
        setColor(event.color || '#66fcf1');
        
        // Si tiene recordatorios configurados
        if (event.reminders && event.reminders.length > 0) {
            setEnableReminder(true);
            // Calcular diferencia en minutos entre el evento y la alerta
            const eventTime = new Date(event.startTime);
            const reminderTime = new Date(event.reminders[0].notificationTime);
            const diffMin = Math.round((eventTime - reminderTime) / 60000);
            setReminderMinutesBefore(diffMin.toString());
        } else {
            setEnableReminder(false);
        }

        setIsModalOpen(true);
    };

    // Guardar (Crear o Editar)
    const handleSaveEvent = async (e) => {
        e.preventDefault();
        if (!title.trim()) return;

        // Calcular hora de notificación
        const reminderTimes = [];
        if (enableReminder) {
            const eventStart = new Date(startTime);
            const alertTime = new Date(eventStart.getTime() - parseInt(reminderMinutesBefore) * 60000);
            reminderTimes.push(alertTime.toISOString());
        }

        const eventDto = {
            title,
            description,
            startTime: new Date(startTime).toISOString(),
            endTime: new Date(endTime).toISOString(),
            location,
            color,
            reminderTimes
        };

        try {
            if (selectedEvent) {
                // Editar evento
                await api.events.update(selectedEvent.id, eventDto);
            } else {
                // Crear evento nuevo
                await api.events.create(eventDto);
            }
            fetchEvents();
            setIsModalOpen(false);
        } catch (err) {
            alert('Error al guardar el evento.');
        }
    };

    // Borrar Evento
    const handleDeleteEvent = async () => {
        if (!selectedEvent) return;
        if (window.confirm('¿Seguro que deseas eliminar este evento?')) {
            try {
                await api.events.delete(selectedEvent.id);
                fetchEvents();
                setIsModalOpen(false);
            } catch (err) {
                alert('No se pudo eliminar el evento.');
            }
        }
    };

    // Mapear eventos a sus respectivas fechas de inicio
    const getEventsForDay = (dayDate) => {
        return events.filter(event => {
            const eventDate = new Date(event.startTime);
            return eventDate.getDate() === dayDate.getDate() &&
                   eventDate.getMonth() === dayDate.getMonth() &&
                   eventDate.getFullYear() === dayDate.getFullYear();
        });
    };

    const days = getDaysInMonth();
    const monthName = currentDate.toLocaleString('es-ES', { month: 'long', year: 'numeric' });
    const weekDays = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];

    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Cargando calendario...</p>
            </div>
        );
    }

    return (
        <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
            {error && <div className="error-banner">{error}</div>}

            {/* ENCABEZADO DEL CALENDARIO */}
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <div>
                    <h1 style={{ fontSize: '2rem', fontWeight: 800, textTransform: 'capitalize' }}>{monthName}</h1>
                    <p style={{ color: 'var(--text-secondary)' }}>Haz clic en cualquier día para programar citas y eventos.</p>
                </div>
                
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button onClick={prevMonth} className="btn btn-secondary" style={{ padding: '0.6rem' }}>
                        <ChevronLeft size={20} />
                    </button>
                    <button onClick={nextMonth} className="btn btn-secondary" style={{ padding: '0.6rem' }}>
                        <ChevronRight size={20} />
                    </button>
                </div>
            </header>

            {/* CUADRÍCULA DEL CALENDARIO */}
            <div className="glass-panel" style={{ padding: '1rem', overflow: 'hidden' }}>
                {/* Cabecera de días de la semana */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', textAlign: 'center', fontWeight: 'bold', paddingBottom: '0.75rem', borderBottom: '1px solid var(--glass-border)' }}>
                    {weekDays.map(wd => (
                        <div key={wd} style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                            {wd}
                        </div>
                    ))}
                </div>

                {/* Días */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gridAutoRows: 'minmax(100px, auto)', gap: '2px', background: 'var(--glass-border)', marginTop: '2px' }}>
                    {days.map((day, idx) => {
                        const dayEvents = getEventsForDay(day.date);
                        return (
                            <div 
                                key={idx} 
                                onClick={() => handleDayClick(day.date)}
                                style={{ 
                                    background: 'var(--bg-app)', 
                                    padding: '0.5rem', 
                                    cursor: 'pointer', 
                                    minHeight: '110px',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '0.25rem',
                                    opacity: day.isCurrentMonth ? 1 : 0.4
                                }}
                            >
                                <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.25rem' }}>
                                    {day.dayNum}
                                </span>
                                
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', flex: 1, overflowY: 'auto' }}>
                                    {dayEvents.map(event => (
                                        <div 
                                            key={event.id}
                                            onClick={(e) => handleEventClick(e, event)}
                                            style={{ 
                                                background: `${event.color}15`, 
                                                borderLeft: `3px solid ${event.color}`, 
                                                color: event.color,
                                                fontSize: '0.75rem',
                                                padding: '4px 6px',
                                                borderRadius: '4px',
                                                fontWeight: 500,
                                                whiteSpace: 'nowrap',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis'
                                            }}
                                        >
                                            {event.title}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>

            {/* MODAL DE CREACIÓN / EDICIÓN */}
            {isModalOpen && (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem' }}>
                    <div className="glass-panel" style={{ width: '100%', maxWidth: '500px', position: 'relative', animation: 'fadeIn 0.3s ease-out' }}>
                        
                        <button onClick={() => setIsModalOpen(false)} style={{ position: 'absolute', top: '1.25rem', right: '1.25rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}>
                            <X size={20} />
                        </button>

                        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '1.5rem' }}>
                            {selectedEvent ? 'Editar Evento' : 'Nuevo Evento'}
                        </h2>

                        <form onSubmit={handleSaveEvent} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div className="form-group">
                                <label>Título del Evento</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    required 
                                    value={title} 
                                    onChange={(e) => setTitle(e.target.value)} 
                                    placeholder="Reunión mensual, cita médica..."
                                />
                            </div>

                            <div className="form-group">
                                <label>Descripción</label>
                                <textarea 
                                    className="form-control" 
                                    value={description} 
                                    onChange={(e) => setDescription(e.target.value)} 
                                    placeholder="Detalles adicionales..."
                                    rows="2"
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                                <div className="form-group">
                                    <label><Clock size={14} /> Inicio</label>
                                    <input 
                                        type="datetime-local" 
                                        className="form-control" 
                                        required 
                                        value={startTime} 
                                        onChange={(e) => setStartTime(e.target.value)} 
                                    />
                                </div>
                                <div className="form-group">
                                    <label><Clock size={14} /> Fin</label>
                                    <input 
                                        type="datetime-local" 
                                        className="form-control" 
                                        required 
                                        value={endTime} 
                                        onChange={(e) => setEndTime(e.target.value)} 
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label><MapPin size={14} /> Ubicación</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    value={location} 
                                    onChange={(e) => setLocation(e.target.value)} 
                                    placeholder="Ej: Sala de juntas, Google Meet..."
                                />
                            </div>

                            {/* Recordatorios por correo */}
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0.5rem 0' }}>
                                <input 
                                    type="checkbox" 
                                    id="enableReminder" 
                                    checked={enableReminder} 
                                    onChange={(e) => setEnableReminder(e.target.checked)} 
                                    style={{ width: '16px', height: '16px', cursor: 'pointer' }}
                                />
                                <label htmlFor="enableReminder" style={{ fontSize: '0.9rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                                    <Bell size={16} color="var(--accent)" />
                                    Recibir alerta por correo electrónico
                                </label>
                            </div>

                            {enableReminder && (
                                <div className="form-group" style={{ animation: 'fadeIn 0.2s' }}>
                                    <label>Enviar alerta antes del evento:</label>
                                    <select 
                                        className="form-control" 
                                        value={reminderMinutesBefore} 
                                        onChange={(e) => setReminderMinutesBefore(e.target.value)}
                                    >
                                        <option value="5">5 minutos antes</option>
                                        <option value="15">15 minutos antes</option>
                                        <option value="30">30 minutos antes</option>
                                        <option value="60">1 hora antes</option>
                                        <option value="1440">24 horas antes</option>
                                    </select>
                                </div>
                            )}

                            <div className="form-group">
                                <label>Color de Etiqueta</label>
                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    {['#66fcf1', '#f59e0b', '#ef4444', '#10b981', '#8b5cf6', '#ec4899'].map(c => (
                                        <button 
                                            key={c} 
                                            type="button" 
                                            onClick={() => setColor(c)}
                                            style={{ 
                                                width: '28px', 
                                                height: '28px', 
                                                borderRadius: '50%', 
                                                background: c, 
                                                border: color === c ? '2px solid white' : 'none',
                                                cursor: 'pointer' 
                                            }}
                                        />
                                    ))}
                                </div>
                            </div>

                            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                                    Guardar
                                </button>
                                {selectedEvent && (
                                    <button type="button" onClick={handleDeleteEvent} className="btn btn-secondary" style={{ color: '#ef4444', borderColor: 'rgba(239, 68, 68, 0.2)' }}>
                                        <Trash2 size={18} />
                                    </button>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
