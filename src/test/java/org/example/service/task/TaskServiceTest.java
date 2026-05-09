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
        TaskRequestDto taskRequestDto = new TaskRequestDto()
                .setName("Build Payroll Module")
                .setDescription("Develop salary calculation module with tax deductions "
                        + "and automated monthly payslip generation")
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProjectId(3L)
                .setAssigneeId(3L)
                .setLabelId(6L);
        Project mockProject = new Project()
                .setId(taskRequestDto.getProjectId());
        User mockUser = new User()
                .setId(taskRequestDto.getAssigneeId())
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(taskRequestDto.getLabelId());
        Task taskWithoutId = new Task()
                .setName(taskRequestDto.getName())
                .setDescription(taskRequestDto.getDescription())
                .setPriority(taskRequestDto.getPriority())
                .setStatus(taskRequestDto.getStatus())
                .setDueDate(taskRequestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        Task saved = new Task()
                .setId(1L)
                .setName(taskWithoutId.getName())
                .setDescription(taskWithoutId.getDescription())
                .setPriority(taskWithoutId.getPriority())
                .setStatus(taskWithoutId.getStatus())
                .setDueDate(taskWithoutId.getDueDate())
                .setProject(taskWithoutId.getProject())
                .setAssignee(taskWithoutId.getAssignee())
                .setLabel(taskWithoutId.getLabel());

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
        TaskRequestDto taskRequestDto = new TaskRequestDto()
                .setName("Build Payroll Module")
                .setDescription("Develop salary calculation module with tax deductions "
                        + "and automated monthly payslip generation")
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProjectId(3L)
                .setAssigneeId(3L)
                .setLabelId(6L);

        assertThrows(EntityNotFoundException.class, () -> taskService.createTask(taskRequestDto));

        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    @DisplayName("""
                Should return all available tasks
                """)
    void getTasksForProject_twoTasksInProject_ReturnsAllTasks() {
        TaskResponseDto buildPayrollSystemTaskDto = TestUtil.BuildPayrollModuleTaskDto();
        Project mockProject = new Project()
                .setId(buildPayrollSystemTaskDto.getProjectId());
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUser");
        Label mockLabel = new Label()
                .setId(1L);
        Task buildPayrollSystemTask = new Task()
                .setId(buildPayrollSystemTaskDto.getId())
                .setName(buildPayrollSystemTaskDto.getName())
                .setDescription(buildPayrollSystemTaskDto.getDescription())
                .setPriority(buildPayrollSystemTaskDto.getPriority())
                .setStatus(buildPayrollSystemTaskDto.getStatus())
                .setDueDate(buildPayrollSystemTaskDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto()
                .setProjectId(mockProject.getId());

        Task addPaymentCountryTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        List<Task> tasks
                = List.of(buildPayrollSystemTask, addPaymentCountryTask);
        Page<Task> page = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(0, 10);

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
        Project project = new Project()
                .setId(taskDto.getProjectId());
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(1L);
        Task task = new Task()
                .setId(taskDto.getId())
                .setName(taskDto.getName())
                .setDescription(taskDto.getDescription())
                .setPriority(taskDto.getPriority())
                .setStatus(taskDto.getStatus())
                .setDueDate(taskDto.getDueDate())
                .setProject(project)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

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
        TaskRequestDto taskRequestDto = new TaskRequestDto()
                .setName("Build Payroll Module")
                .setDescription("Develop salary calculation module with tax deductions "
                        + "and automated monthly payslip generation")
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProjectId(3L)
                .setAssigneeId(3L)
                .setLabelId(6L);
        Project mockProject = new Project()
                .setId(taskRequestDto.getProjectId());
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(6L);
        Task taskWithoutId = new Task()
                .setName(taskRequestDto.getName())
                .setDescription(taskRequestDto.getDescription())
                .setPriority(taskRequestDto.getPriority())
                .setStatus(taskRequestDto.getStatus())
                .setDueDate(taskRequestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        Task updated = new Task()
                .setId(1L)
                .setName(taskWithoutId.getName())
                .setDescription(taskWithoutId.getDescription())
                .setPriority(taskWithoutId.getPriority())
                .setStatus(taskWithoutId.getStatus())
                .setDueDate(taskWithoutId.getDueDate())
                .setProject(taskWithoutId.getProject())
                .setAssignee(taskWithoutId.getAssignee())
                .setLabel(taskWithoutId.getLabel());

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
        when(projectRepository.findByAssigneeIdAndId(1L, taskRequestDto.getProjectId()))
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
        TaskRequestDto taskRequestDto = new TaskRequestDto()
                .setName("Build Payroll Module")
                .setDescription("Develop salary calculation module with tax deductions "
                + "and automated monthly payslip generation")
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProjectId(3L)
                .setAssigneeId(3L)
                .setLabelId(1L);
        Project mockProject = new Project()
                .setId(taskRequestDto.getProjectId());
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(1L);
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
        TaskResponseDto taskDto = TestUtil.AddPaymentCountryTaskDto();
        Project mockProject = new Project()
                .setId(1L);
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(1L);
        Task task = new Task()
                .setId(taskDto.getId())
                .setName(taskDto.getName())
                .setDescription(taskDto.getDescription())
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

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
        TaskResponseDto buildPayrollSystemTaskDto = TestUtil.BuildPayrollModuleTaskDto();
        Project buildPayrollSystemProject = new Project()
                .setId(buildPayrollSystemTaskDto.getProjectId());
        User mockUser = new User()
                .setId(1L);
        Label mockLabel = new Label()
                .setId(1L);
        Task buildPayrollSystemTask = new Task()
                .setId(buildPayrollSystemTaskDto.getId())
                .setName(buildPayrollSystemTaskDto.getName())
                .setDescription(buildPayrollSystemTaskDto.getDescription())
                .setPriority(buildPayrollSystemTaskDto.getPriority())
                .setStatus(buildPayrollSystemTaskDto.getStatus())
                .setDueDate(buildPayrollSystemTaskDto.getDueDate())
                .setProject(buildPayrollSystemProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        Project addPaymentCountryProject = new Project()
                .setId(addPaymentCountryTaskDto.getProjectId());

        Task addPaymentCountryTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(addPaymentCountryProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

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
        TaskResponseDto buildPayrollSystemTaskDto = TestUtil.BuildPayrollModuleTaskDto();
        Project buildPayrollSystemProject = new Project()
                .setId(buildPayrollSystemTaskDto.getProjectId());
        User mockUser = new User()
                .setId(1L);
        Label mockLabel = new Label()
                .setId(1L);
        Task buildPayrollSystemTask = new Task()
                .setId(buildPayrollSystemTaskDto.getId())
                .setName(buildPayrollSystemTaskDto.getName())
                .setDescription(buildPayrollSystemTaskDto.getDescription())
                .setPriority(buildPayrollSystemTaskDto.getPriority())
                .setStatus(buildPayrollSystemTaskDto.getStatus())
                .setDueDate(buildPayrollSystemTaskDto.getDueDate())
                .setProject(buildPayrollSystemProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        Project addPaymentCountryProject = new Project()
                .setId(addPaymentCountryTaskDto.getProjectId());

        Task addPaymentCountryTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(addPaymentCountryProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);

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
