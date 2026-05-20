package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.TaskDTO;
import com.agenda.interactiva.model.Task;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.service.TaskService;
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
 * Controlador REST para gestionar Tareas pendientes.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Endpoints para la gestión de tareas, prioridades y fechas límite.")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todas las tareas", description = "Devuelve el listado de tareas del usuario autenticado.")
    public ResponseEntity<List<Task>> getAllTasks() {
        User user = userService.getAuthenticatedUser();
        List<Task> tasks = taskService.getAllTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarea por ID", description = "Devuelve una tarea específica si pertenece al usuario.")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        Task task = taskService.getTaskById(id, user);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Crear nueva tarea", description = "Registra una tarea con su prioridad y estado inicial de completado.")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDTO dto) {
        User user = userService.getAuthenticatedUser();
        Task createdTask = taskService.createTask(dto, user);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tarea", description = "Edita datos de una tarea, incluyendo marcarla como completada.")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO dto) {
        User user = userService.getAuthenticatedUser();
        Task updatedTask = taskService.updateTask(id, dto, user);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tarea", description = "Realiza el borrado lógico de una tarea.")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}
