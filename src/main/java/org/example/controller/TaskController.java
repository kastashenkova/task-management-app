package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.project.ProjectResponseDto;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.repository.project.specification.ProjectSearchParameters;
import org.example.repository.task.specification.TaskSearchParameters;
import org.example.service.task.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tasks management",
        description = "Endpoints for managing tasks")
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task",
            description = "Create a new task")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve tasks for a project",
            description = "Retrieve tasks for a project by its id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Page<TaskResponseDto> getTasksForProject(@RequestParam Long projectId,
                                                    Pageable pageable) {
        return taskService.getTasksForProject(projectId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve task details",
            description = "Retrieve task details by its id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task",
            description = "Update task by its id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TaskResponseDto updateTaskById(@PathVariable Long id,
                                          @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.updateTaskById(id, taskRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task",
            description = "Delete task by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks",
            description = "Get a list of all available tasks by certain parameter")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponseDto> search(TaskSearchParameters searchParameters, Pageable pageable) {
        return taskService.search(searchParameters, pageable);
    }
}
