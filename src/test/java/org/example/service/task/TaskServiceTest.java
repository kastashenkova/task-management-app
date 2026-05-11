package org.example.service.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.mapper.TaskMapper;
import org.example.model.label.Label;
import org.example.model.project.Project;
import org.example.model.task.Priority;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.label.LabelRepository;
import org.example.repository.project.ProjectRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.task.specification.TaskSearchParameters;
import org.example.repository.task.specification.TaskSpecificationBuilder;
import org.example.repository.user.UserRepository;
import org.example.service.third_party.CalendarEventResult;
import org.example.service.third_party.google_calendar.GoogleCalendarService;
import org.example.service.third_party.WhatsAppService;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private TaskSpecificationBuilder taskSpecificationBuilder;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private WhatsAppService whatsAppService;

    @Test
    @DisplayName("""
                Should create a new Task
                """)
    void createTask_newTask_ReturnsNewTask() throws Exception {
        // given
        TaskRequestDto taskRequestDto = TestUtil.BuildPayrollModuleTaskRequestDto();
        Task saved = TestUtil.BuildPayrollModuleTask();

        Project mockProject = saved.getProject();
        User mockUser = saved.getAssignee();
        Label mockLabel = saved.getLabel();
        Task taskWithoutId = new Task()
                .setName(taskRequestDto.getName())
                .setDescription(taskRequestDto.getDescription())
                .setPriority(taskRequestDto.getPriority())
                .setStatus(taskRequestDto.getStatus())
                .setDueDate(taskRequestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        TaskResponseDto expected = TestUtil.BuildPayrollModuleTaskDto();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));

        when(taskMapper.toEntity(taskRequestDto)).thenReturn(taskWithoutId);
        when(projectRepository.findById(taskRequestDto.getProjectId()))
                .thenReturn(Optional.of(mockProject));
        when(userRepository.findById(taskRequestDto.getAssigneeId()))
                .thenReturn(Optional.of(mockUser));
        when(labelRepository.findById(taskRequestDto.getLabelId()))
                .thenReturn(Optional.of(mockLabel));

        CalendarEventResult mockResult = new CalendarEventResult(
                "eventId", "http://event-url");
        when(googleCalendarService.createEvent(any(Task.class), anyLong())).thenReturn(mockResult);

        doNothing().when(whatsAppService).sendTaskAssignmentWhatsApp(any(), any(), any());

        when(taskRepository.save(any(Task.class))).thenReturn(saved);
        when(taskMapper.toDto(any(Task.class))).thenReturn(expected);

        // when
        TaskResponseDto actual = taskService.createTask(taskRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("""
                Should return Not Found
                """)
    void createTask_nonExistingForeignKeyEntity_ReturnsNotFound() throws Exception {
        TaskRequestDto taskRequestDto = TestUtil.BuildPayrollModuleTaskRequestDto();

        assertThrows(EntityNotFoundException.class, () -> taskService.createTask(taskRequestDto));

        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    @DisplayName("""
                Should return all available tasks
                """)
    void getTasksForProject_twoTasksInProject_ReturnsAllTasks() {
        TaskResponseDto buildPayrollSystemTaskDto = TestUtil.BuildPayrollModuleTaskDto();
        Task buildPayrollSystemTask = TestUtil.BuildPayrollModuleTask();

        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto()
                .setProjectId(buildPayrollSystemTaskDto.getProjectId());
        Task addPaymentCountryTask = TestUtil.AddPaymentCountryTask()
                .setProject(buildPayrollSystemTask.getProject());

        List<Task> tasks
                = List.of(buildPayrollSystemTask, addPaymentCountryTask);
        Page<Task> page = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(0, 10);

        Project mockProject = buildPayrollSystemTask.getProject();
        User mockUser = buildPayrollSystemTask.getAssignee();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(projectRepository.findByAssigneeIdAndId(mockUser.getId(), mockProject.getId()))
                .thenReturn(Optional.of(mockProject));
        when(taskMapper.toDto(addPaymentCountryTask))
                .thenReturn(addPaymentCountryTaskDto);
        when(taskMapper.toDto(buildPayrollSystemTask))
                .thenReturn(buildPayrollSystemTaskDto);
        when(taskRepository.findAllByProject_Id(mockProject.getId(), pageable)).thenReturn(page);

        Page<TaskResponseDto> actual = taskService.getTasksForProject(
                mockProject.getId(), pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(taskRepository, times(1))
                .findAllByProject_Id(eq(mockProject.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return Not Found
                """)
    void getTasksForProject_nonExistingProject_ReturnsNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Long nonExistingProjectId = 10L;
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        assertThrows(EntityNotFoundException.class,
                () -> taskService.getTasksForProject(nonExistingProjectId,  pageable));

        verify(taskRepository, times(0))
                .findAllByProject_Id(nonExistingProjectId, pageable);
        verify(projectRepository, times(1))
                .findByAssigneeIdAndId(mockUser.getId(), nonExistingProjectId);
    }

    @Test
    @DisplayName("""
                Should return empty page
                """)
    void getTasksForProject_noTasks_ReturnsEmptyPage() {
        List<Task> tasks = List.of();
        Page<Task> page = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(0, 10);

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        Long mockProjectId = 1L;
        Project mockProject = new Project()
                .setId(mockProjectId);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findByAssigneeIdAndId(mockUser.getId(), mockProjectId))
                .thenReturn(Optional.of(mockProject));
        when(taskRepository.findAllByProject_Id(mockProjectId, pageable)).thenReturn(page);

        Page<TaskResponseDto> actual = taskService.getTasksForProject(mockProjectId, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());
        verify(taskRepository, times(1))
                .findAllByProject_Id(eq(mockProjectId), any(Pageable.class));
    }
    @Test
    @DisplayName("""
                Get existing Task by its id
                """)
    void getTaskById_existingTask_ReturnsTheTask() {
        TaskResponseDto taskDto = TestUtil.BuildPayrollModuleTaskDto();
        Task task = TestUtil.BuildPayrollModuleTask();

        User mockUser = task.getAssignee();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(3L, mockUser))
                .thenReturn(Optional.of(task));
        when(taskMapper.toDto(task))
                .thenReturn(taskDto);

        TaskResponseDto actual = taskService.getTaskById(3L);

        assertNotNull(actual);
        verify(taskRepository, times(1))
                .findTaskByIdAndAssignee(3L, mockUser);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void getTaskById_nonExistingTask_NotFound() {
        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));

        Long nonExistingTaskId = 9999L;
        assertThrows(EntityNotFoundException.class,
                () -> taskService.getTaskById(nonExistingTaskId));

        verify(taskRepository, times(1))
                .findTaskByIdAndAssignee(nonExistingTaskId, mockUser);
    }


    @Test
    @DisplayName("""
                Should return existing Task with updated info
                """)
    void updateTaskById_existingTask_ReturnsUpdatedTask() throws Exception {
        // given
        TaskRequestDto taskRequestDto = TestUtil.BuildPayrollModuleTaskRequestDto();
        Task updated = TestUtil.BuildPayrollModuleTask();

        Project mockProject = updated.getProject();
        User mockUser = updated.getAssignee();
        Label mockLabel = updated.getLabel();
        Task taskWithoutId = new Task()
                .setName(taskRequestDto.getName())
                .setDescription(taskRequestDto.getDescription())
                .setPriority(taskRequestDto.getPriority())
                .setStatus(taskRequestDto.getStatus())
                .setDueDate(taskRequestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        TaskResponseDto expected = TestUtil.BuildPayrollModuleTaskDto();
        expected.setStatus(taskRequestDto.getStatus());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        doNothing().when(googleCalendarService).updateEvent(any(Task.class), anyLong());
        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(userRepository.findById(taskRequestDto.getAssigneeId()))
                .thenReturn(Optional.of(mockUser));
        when(projectRepository.findByAssigneeIdAndId(3L, taskRequestDto.getProjectId()))
                .thenReturn(Optional.of(mockProject));
        when(labelRepository.findById(mockLabel.getId()))
                .thenReturn(Optional.of(mockLabel));
        when(taskRepository.findTaskByIdAndAssignee(1L, mockUser))
                .thenReturn(Optional.of(taskWithoutId));
        when(taskRepository.save(taskWithoutId)).thenReturn(updated);
        when(taskMapper.toDto(any(Task.class))).thenReturn(expected);

        // when
        TaskResponseDto actual = taskService.updateTaskById(
                1L,
                taskRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(taskRepository, times(1)).save(taskWithoutId);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void updateTaskById_nonExistingTask_NotFound() throws Exception {
        TaskRequestDto taskRequestDto = TestUtil.BuildPayrollModuleTaskRequestDto();
        Task updated = TestUtil.BuildPayrollModuleTask();

        Project mockProject = updated.getProject();
        User mockUser = updated.getAssignee();
        Label mockLabel = updated.getLabel();
        Task taskWithoutId = new Task()
                .setName(taskRequestDto.getName())
                .setDescription(taskRequestDto.getDescription())
                .setPriority(taskRequestDto.getPriority())
                .setStatus(taskRequestDto.getStatus())
                .setDueDate(taskRequestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        Long nonExistingTaskId = 10L;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));

        assertThrows(EntityNotFoundException.class,
                () -> taskService.updateTaskById(nonExistingTaskId, taskRequestDto));

        verify(taskRepository, times(0)).save(taskWithoutId);
    }

    @Test
    @DisplayName("""
                Should delete existing Task by its id
                """)
    void deleteTaskById_existingTask_Success() throws Exception {
        Task task = TestUtil.AddPaymentCountryTask();

        User mockUser = task.getAssignee();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        doNothing().when(googleCalendarService).deleteEvent(task, mockUser.getId());

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        taskService.deleteTaskById(1L);

        verify(taskRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void deleteTaskById_nonExistingTask_NotFound() throws Exception {
        Long nonExistingTaskId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteTaskById(nonExistingTaskId));

        verify(taskRepository, times(0)).deleteById(anyLong());
    }

    @Test
    @DisplayName("""
                 Search tasks by the same priority
                 """)
    void search_byPriority_ReturnsTasks() {
        Task buildPayrollSystemTask = TestUtil.BuildPayrollModuleTask();
        Task addPaymentCountryTask = TestUtil.AddPaymentCountryTask();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(
                List.of(buildPayrollSystemTask, addPaymentCountryTask), pageable, 2
        );

        String[] requiredPriorities = {"MEDIUM", "HIGH"};
        TaskSearchParameters searchParameters
                = new TaskSearchParameters(requiredPriorities, null);
        Specification<Task> taskSpecification = mock(Specification.class); // stub

        when(taskSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(taskSpecification);
        when(taskRepository.findAll(taskSpecification, pageable))
                .thenReturn(page);

        Page<TaskResponseDto> actual = taskService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(taskSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(taskRepository, times(1))
                .findAll(taskSpecification, pageable);
    }

    @Test
    @DisplayName("""
                 Search tasks by the same status
                 """)
    void search_byStatus_ReturnsTasks() {
        Task buildPayrollSystemTask = TestUtil.BuildPayrollModuleTask();
        Task addPaymentCountryTask = TestUtil.AddPaymentCountryTask();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(
                List.of(buildPayrollSystemTask, addPaymentCountryTask), pageable, 2
        );

        String[] requiredStatuses = {"IN_PROGRESS", "NOT_STARTED"};
        TaskSearchParameters searchParameters
                = new TaskSearchParameters(null, requiredStatuses);
        Specification<Task> taskSpecification = mock(Specification.class); // stub

        when(taskSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(taskSpecification);
        when(taskRepository.findAll(taskSpecification, pageable))
                .thenReturn(page);

        Page<TaskResponseDto> actual = taskService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(taskSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(taskRepository, times(1))
                .findAll(taskSpecification, pageable);
    }

    @Test
    @DisplayName("""
                 Should return empty page
                 """)
    void search_byStatusWithNoSuchTask_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(
                List.of(), pageable, 0
        );

        String[] requiredStatuses = {"IN_PROGRESS"};
        TaskSearchParameters searchParameters
                = new TaskSearchParameters(null, requiredStatuses);
        Specification<Task> taskSpecification = mock(Specification.class); // stub

        when(taskSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(taskSpecification);
        when(taskRepository.findAll(taskSpecification, pageable))
                .thenReturn(page);

        Page<TaskResponseDto> actual = taskService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());

        verify(taskSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(taskRepository, times(1))
                .findAll(taskSpecification, pageable);
    }
}
