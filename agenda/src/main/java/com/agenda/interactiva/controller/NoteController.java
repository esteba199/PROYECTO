package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.NoteDTO;
import com.agenda.interactiva.model.Note;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.service.NoteService;
import com.agenda.interactiva.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar Notas rápidas.
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Endpoints para la gestión de notas rápidas y post-its.")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todas las notas", description = "Devuelve todas las notas rápidas del usuario autenticado.")
    public ResponseEntity<List<Note>> getAllNotes() {
        User user = userService.getAuthenticatedUser();
        List<Note> notes = noteService.getAllNotes(user);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID", description = "Devuelve una nota específica si pertenece al usuario.")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        Note note = noteService.getNoteById(id, user);
        return ResponseEntity.ok(note);
    }

    @PostMapping
    @Operation(summary = "Crear nueva nota", description = "Guarda una nueva nota rápida.")
    public ResponseEntity<Note> createNote(@Valid @RequestBody NoteDTO dto) {
        User user = userService.getAuthenticatedUser();
        Note createdNote = noteService.createNote(dto, user);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota", description = "Edita el título o contenido de una nota rápida.")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @Valid @RequestBody NoteDTO dto) {
        User user = userService.getAuthenticatedUser();
        Note updatedNote = noteService.updateNote(id, dto, user);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota", description = "Realiza el borrado lógico de una nota.")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        noteService.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }
}
