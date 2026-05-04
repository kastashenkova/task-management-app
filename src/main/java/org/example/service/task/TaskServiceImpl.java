package org.example.service.task;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.exception.GoogleCalendarException;
import org.example.mapper.TaskMapper;
import org.example.model.label.Label;
import org.example.model.project.Project;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.label.LabelRepository;
import org.example.repository.project.ProjectRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.task.specification.TaskSearchParameters;
import org.example.repository.task.specification.TaskSpecificationBuilder;
import org.example.repository.user.UserRepository;
import org.example.service.third_party.CalendarEventResult;
import org.example.service.third_party.WhatsAppService;
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
    private final WhatsAppService whatsAppService;
    private final LabelRepository labelRepository;

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

        Label label = labelRepository.findById(taskRequestDto.getLabelId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Label not found: " + taskRequestDto.getLabelId()
                        ));
        task.setLabel(label);

        taskRepository.save(task);

        CalendarEventResult event = null;
        try {
            Long adminId = getCurrentUser().getId();
            event = googleCalendarService.createEvent(task, adminId);
            task.setCalendarEventId(event.eventId());
        } catch (Exception e) {
            System.err.println("Error while saving event: " + e);
        }

        if (event == null) {
            throw new GoogleCalendarException("Calendar event not found: " + task.getCalendarEventId());
        }

        try {
            whatsAppService.sendTaskAssignmentWhatsApp(task, assignee, event.eventUrl());
        } catch (Exception e) {
            System.err.println("WhatsApp message failed: " + e);
        }

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

        Label label = labelRepository.findById(taskRequestDto.getLabelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Label not found: " + taskRequestDto.getLabelId()
                ));
        task.setLabel(label);

        try {
            Long adminId = getCurrentUser().getId();
            googleCalendarService.updateEvent(task, adminId);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating event: " + e.getMessage());
        }

        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with such id not found: " + id)
        );

        try {
            Long adminId = getCurrentUser().getId();
            googleCalendarService.deleteEvent(task, adminId);
        } catch (IOException e) {
            System.err.println("Error while deleting event: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting event: " + e.getMessage());
        }

        taskRepository.deleteTaskById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
