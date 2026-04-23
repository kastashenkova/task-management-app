package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.service.task.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
    public TaskResponseDto createTask(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve tasks for a project",
            description = "Retrieve tasks for a project by its id")
    public Page<TaskResponseDto> getTasksForProject(@RequestParam Long projectId,
                                                    Pageable pageable) {
        return taskService.getTasksForProject(projectId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve task details",
            description = "Retrieve task details by its id")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task",
            description = "Update task by its id")
    public TaskResponseDto updateTaskById(@PathVariable Long id,
                                          @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.updateTaskById(id, taskRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task",
            description = "Delete task by its id")
    public void deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}
