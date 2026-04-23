package org.example.service.task;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.mapper.TaskMapper;
import org.example.model.project.Project;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.project.ProjectRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Task task = taskMapper.toEntity(taskRequestDto);

        Project project = projectRepository.findById(taskRequestDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project not found: " + taskRequestDto.getProjectId()));
        task.setProject(project);

        User assignee = userRepository.findById(taskRequestDto.getAssigneeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found: " + taskRequestDto.getAssigneeId()));
        task.setAssignee(assignee);

        taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    @Override
    public Page<TaskResponseDto> getTasksForProject(Long projectId, Pageable pageable) {
        projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Project with such id not found: " + projectId));
        return taskRepository.findAllByProject_Id(projectId, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with such id not found: " + id));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskById(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with such id not found: " + id));
        task.setName(taskRequestDto.getName());
        task.setDescription(taskRequestDto.getDescription());
        task.setPriority(taskRequestDto.getPriority());
        task.setStatus(taskRequestDto.getStatus());
        task.setDueDate(taskRequestDto.getDueDate());

        Project project = projectRepository.findById(taskRequestDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with such id not found: " + taskRequestDto.getProjectId()));
        task.setProject(project);

        User assignee = userRepository.findById(taskRequestDto.getAssigneeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with such id not found: " + taskRequestDto.getAssigneeId()));
        task.setAssignee(assignee);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Task with such id not found: " + id);
        }
        taskRepository.deleteTaskById(id);
    }
}
