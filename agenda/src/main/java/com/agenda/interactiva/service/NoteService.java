package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.NoteDTO;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.Note;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para gestionar las Notas rápidas del usuario.
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<Note> getAllNotes(User user) {
        return noteRepository.findByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public Note getNoteById(Long noteId, User user) {
        return noteRepository.findById(noteId)
                .filter(note -> note.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("La nota solicitada no existe o no tienes permisos de acceso."));
    }

    @Transactional
    public Note createNote(NoteDTO dto, User user) {
        Note note = Note.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .color(dto.getColor())
                .build();
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long noteId, NoteDTO dto, User user) {
        Note note = getNoteById(noteId, user);
        
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        if (dto.getColor() != null) {
            note.setColor(dto.getColor());
        }
        
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId, User user) {
        Note note = getNoteById(noteId, user);
        noteRepository.delete(note); // Realiza soft delete
    }
}
