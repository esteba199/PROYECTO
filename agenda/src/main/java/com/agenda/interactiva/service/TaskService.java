package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.TaskDTO;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.Task;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para gestionar las Tareas pendientes.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId, User user) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("La tarea solicitada no existe o no tienes permisos de acceso."));
    }

    @Transactional
    public Task createTask(TaskDTO dto, User user) {
        Task task = Task.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .isCompleted(dto.getIsCompleted() != null ? dto.getIsCompleted() : false)
                .priority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM")
                .build();
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, TaskDTO dto, User user) {
        Task task = getTaskById(taskId, user);
        
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        
        if (dto.getIsCompleted() != null) {
            task.setIsCompleted(dto.getIsCompleted());
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId, User user) {
        Task task = getTaskById(taskId, user);
        taskRepository.delete(task); // Realiza soft delete
    }
}
