package org.example.service.task;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.exception.EmailMessageException;
import org.example.exception.GoogleCalendarException;
import org.example.mapper.TaskMapper;
import org.example.model.project.Project;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.project.ProjectRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.task.specification.TaskSearchParameters;
import org.example.repository.task.specification.TaskSpecificationBuilder;
import org.example.repository.user.UserRepository;
import org.example.service.third_party.EmailService;
import org.example.service.third_party.GoogleCalendarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskSpecificationBuilder taskSpecificationBuilder;
    private final GoogleCalendarService googleCalendarService;
    private final EmailService emailService;

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

        try {
            emailService.sendTaskAssignmentEmail(task, assignee);
        } catch (Exception e) {
            System.err.println("Email failed: " + e);
        }

        try {
            String eventId = googleCalendarService.createEvent(task, taskRequestDto.getAssigneeId());
            task.setCalendarEventId(eventId);
            taskRepository.save(task);
        } catch (Exception e) {
            System.err.println("Error while saving event: " + e);
        }

        return taskMapper.toDto(task);
    }

    @Override
    public Page<TaskResponseDto> getTasksForProject(Long projectId, Pageable pageable) {
        if (!isAdmin()) {
            User user = getCurrentUser();
            projectRepository.findByAssigneeIdAndId(user.getId(), projectId).orElseThrow(
                    () -> new EntityNotFoundException("Project with id " + projectId + " for user "
                            + user.getId() + " not found")
            );
        } else {
            projectRepository.findById(projectId).orElseThrow(
                    () -> new EntityNotFoundException("Project with such id not found: " + projectId));
        }
        return taskRepository.findAllByProject_Id(projectId, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        return taskMapper.toDto(getById(id));
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskById(Long id, TaskRequestDto taskRequestDto) {
        Task task = getById(id);

        task.setName(taskRequestDto.getName());
        task.setDescription(taskRequestDto.getDescription());
        task.setPriority(taskRequestDto.getPriority());
        task.setStatus(taskRequestDto.getStatus());
        task.setDueDate(taskRequestDto.getDueDate());

        Project project;
        if (!isAdmin()) {
            User user = getCurrentUser();
            project = projectRepository.findByAssigneeIdAndId(user.getId(),
                    taskRequestDto.getProjectId()).orElseThrow(
                    () -> new EntityNotFoundException(
                            "Project with id " + taskRequestDto.getProjectId()
                                    + " for user " + user.getId() + " not found")
            );
        } else {
            project = projectRepository.findById(taskRequestDto.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Project with such id not found: " + taskRequestDto.getProjectId()));
        }
        task.setProject(project);

        User assignee = userRepository.findById(taskRequestDto.getAssigneeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with such id not found: " + taskRequestDto.getAssigneeId()));
        task.setAssignee(assignee);

        try {
            googleCalendarService.updateEvent(task, taskRequestDto.getAssigneeId());
            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating event: " + e.getMessage());
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with such id not found: " + id)
        );

        try {
            googleCalendarService.deleteEvent(task, task.getAssignee().getId());
        } catch (IOException e) {
            System.err.println("Error while saving event: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting event: " + e.getMessage());
        }

        taskRepository.deleteTaskById(id);
    }

    @Override
    public Page<TaskResponseDto> search(TaskSearchParameters searchParameters, Pageable pageable) {
        Specification<Task> taskSpecification = taskSpecificationBuilder
                .buildSpecification(searchParameters);
        return taskRepository.findAll(taskSpecification, pageable)
                .map(taskMapper::toDto);
    }

    private Task getById(Long id) {
        Task task;
        if (!isAdmin()) {
            User user = getCurrentUser();
            task = taskRepository.findTaskByIdAndAssignee(id, user).orElseThrow(
                    () -> new EntityNotFoundException("Task with id " + id + " for user "
                            + user.getId() + " not found")
            );
        } else {
            task = taskRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Task with such id not found: " + id));
        }
        return task;
    }

    private boolean isAdmin() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}
