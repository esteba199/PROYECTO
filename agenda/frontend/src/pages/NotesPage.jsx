import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Trash2, Edit3, Archive, Palette } from 'lucide-react';

/**
 * Página de Notas Rápidas (Post-its / Notion board style).
 * Permite guardar ideas repentinas y apuntes rápidos.
 */
export default function NotesPage() {
    const [notes, setNotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estados de Formulario de Creación/Edición
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingNote, setEditingNote] = useState(null);
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [color, setColor] = useState('#1e293b'); // Color pizarra por defecto

    // Paleta de colores premium para post-its
    const noteColors = [
        { name: 'Pizarra', hex: '#1e293b' },
        { name: 'Esmeralda', hex: '#064e3b' },
        { name: 'Sangría', hex: '#7f1d1d' },
        { name: 'Índigo', hex: '#311042' },
        { name: 'Ámbar', hex: '#78350f' },
        { name: 'Rosa', hex: '#831843' }
    ];

    useEffect(() => {
        fetchNotes();
    }, []);

    const fetchNotes = async () => {
        try {
            const data = await api.notes.getAll();
            setNotes(data || []);
        } catch (err) {
            setError('Error al recuperar notas rápidas.');
        } finally {
            setLoading(false);
        }
    };

    const handleSaveNote = async (e) => {
        e.preventDefault();
        if (!title.trim()) return;

        const noteDto = { title, content, color };

        try {
            if (editingNote) {
                const updated = await api.notes.update(editingNote.id, noteDto);
                setNotes(notes.map(n => n.id === editingNote.id ? updated : n));
            } else {
                const created = await api.notes.create(noteDto);
                setNotes([...notes, created]);
            }
            resetForm();
        } catch (err) {
            alert('Error al guardar la nota.');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Seguro que deseas eliminar esta nota rápida?')) {
            try {
                await api.notes.delete(id);
                setNotes(notes.filter(n => n.id !== id));
            } catch (err) {
                alert('No se pudo borrar la nota.');
            }
        }
    };

    const openEditForm = (note) => {
        setEditingNote(note);
        setTitle(note.title);
        setContent(note.content || '');
        setColor(note.color || '#1e293b');
        setIsFormOpen(true);
    };

    const resetForm = () => {
        setIsFormOpen(false);
        setEditingNote(null);
        setTitle('');
        setContent('');
        setColor('#1e293b');
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p>Cargando notas...</p>
            </div>
        );
    }

    return (
        <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
            {error && <div className="error-banner">{error}</div>}

            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <div>
                    <h1 style={{ fontSize: '2rem', fontWeight: 800 }}>Notas Rápidas</h1>
                    <p style={{ color: 'var(--text-secondary)' }}>Escribe ideas, enlaces rápidos, contraseñas o recordatorios informales.</p>
                </div>
                
                <button onClick={() => setIsFormOpen(true)} className="btn btn-primary">
                    <Plus size={18} />
                    Crear Nota
                </button>
            </header>

            {/* FORMULARIO DE EDICIÓN / CREACIÓN */}
            {isFormOpen && (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem' }}>
                    <div className="glass-panel" style={{ width: '100%', maxWidth: '500px', animation: 'fadeIn 0.3s ease-out' }}>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '1.5rem' }}>
                            {editingNote ? 'Editar Nota' : 'Nueva Nota'}
                        </h2>

                        <form onSubmit={handleSaveNote} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div className="form-group">
                                <label>Título</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    required 
                                    value={title} 
                                    onChange={(e) => setTitle(e.target.value)} 
                                    placeholder="Ej: Ideas de regalo..."
                                />
                            </div>

                            <div className="form-group">
                                <label>Contenido</label>
                                <textarea 
                                    className="form-control" 
                                    value={content} 
                                    onChange={(e) => setContent(e.target.value)} 
                                    placeholder="Contenido de la nota..."
                                    rows="5"
                                />
                            </div>

                            <div className="form-group">
                                <label><Palette size={14} /> Color de fondo</label>
                                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                                    {noteColors.map(c => (
                                        <button 
                                            key={c.hex} 
                                            type="button" 
                                            onClick={() => setColor(c.hex)}
                                            style={{ 
                                                width: '32px', 
                                                height: '32px', 
                                                borderRadius: '8px', 
                                                background: c.hex, 
                                                border: color === c.hex ? '2px solid white' : '1px solid rgba(255,255,255,0.1)',
                                                cursor: 'pointer' 
                                            }}
                                            title={c.name}
                                        />
                                    ))}
                                </div>
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

            {/* TABLERO DE NOTAS (POST-ITS) */}
            {notes.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-secondary)' }}>
                    <Plus size={40} style={{ marginBottom: '1rem', color: 'var(--text-secondary)' }} />
                    <p>No tienes notas guardadas. ¡Crea una ahora!</p>
                </div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '1.5rem' }}>
                    {notes.map(note => (
                        <article 
                            key={note.id} 
                            className="glass-panel" 
                            style={{ 
                                background: note.color ? `${note.color}80` : 'var(--glass-bg)', // Transparente
                                display: 'flex', 
                                flexDirection: 'column', 
                                gap: '1rem',
                                minHeight: '180px',
                                justifyContent: 'space-between',
                                border: '1px solid rgba(255, 255, 255, 0.1)'
                            }}
                        >
                            <div>
                                <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                                    {note.title}
                                </h3>
                                <p style={{ fontSize: '0.9rem', color: 'rgba(255, 255, 255, 0.8)', whiteSpace: 'pre-wrap', lineHeight: '1.4' }}>
                                    {note.content}
                                </p>
                            </div>

                            <footer style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid rgba(255,255,255,0.08)', paddingTop: '0.75rem' }}>
                                <span style={{ fontSize: '0.75rem', color: 'rgba(255,255,255,0.5)' }}>
                                    {new Date(note.createdAt).toLocaleDateString('es-ES')}
                                </span>
                                
                                <div style={{ display: 'flex', gap: '0.25rem' }}>
                                    <button onClick={() => openEditForm(note)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'white', opacity: 0.7, padding: '4px' }}>
                                        <Edit3 size={16} />
                                    </button>
                                    <button onClick={() => handleDelete(note.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#fca5a5', opacity: 0.7, padding: '4px' }}>
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </footer>
                        </article>
                    ))}
                </div>
            )}
        </div>
    );
}
