package org.example.service.task;

import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.repository.task.specification.TaskSearchParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto taskRequestDto);

    Page<TaskResponseDto> getTasksForProject(Long projectId, Pageable pageable);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTaskById(Long id, TaskRequestDto taskRequestDto);

    void deleteTaskById(Long id);

    Page<TaskResponseDto> search(TaskSearchParameters searchParameters, Pageable pageable);
}
