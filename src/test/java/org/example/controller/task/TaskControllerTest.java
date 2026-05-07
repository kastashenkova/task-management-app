package org.example.controller.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.model.task.Priority;
import org.example.model.task.Status;
import org.example.service.third_party.CalendarEventResult;
import org.example.service.third_party.GoogleCalendarService;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean // in order to avoid real Google Calendar connection
    private GoogleCalendarService googleCalendarService;

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available tasks within the specific project
           """)
    void getTasks_twoTasksWithinProject_ReturnsTwoTasks() throws Exception {
        MvcResult result = mockMvc.perform(get("/tasks?projectId=1"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<TaskResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, TaskResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(2, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return empty list
           """)
    void getTasks_noTasksWithinProject_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/tasks?projectId=2"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<TaskResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, TaskResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return specific task by its id
           """)
    void getTaskById_thirdTask_ReturnsTheThirdTaskInDto() throws Exception {
        TaskResponseDto expected = TestUtil.BuildPayrollModuleTaskDto();

        MvcResult result = mockMvc.perform(get("/tasks/{id}",
                        3L))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TaskResponseDto actual = objectMapper.readValue(
                json,
                TaskResponseDto.class
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return Not Found
           """)
    void getTaskById_invalidTaskId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}",
                        10L))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "john.carter", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Create a new Task within the specific project
           """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createTask_validRequestDto_ReturnsCreatedTask() throws Exception {
        // in order to avoid real Google Calendar connection
        CalendarEventResult mockEvent = new CalendarEventResult("test-event-id",
                "test-link");
        when(googleCalendarService.createEvent(any(), any())).thenReturn(mockEvent);

        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setName("Add Payment Country");
        requestDto.setDescription("Add country #170 to the payment system");
        requestDto.setPriority(Priority.HIGH);
        requestDto.setStatus(org.example.model.task.Status.IN_PROGRESS);
        requestDto.setDueDate(LocalDate.of(2026, 10, 26));
        requestDto.setProjectId(2L);
        requestDto.setAssigneeId(3L);
        requestDto.setLabelId(11L);

        TaskResponseDto expected = TestUtil.AddPaymentCountryTaskDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/tasks")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), TaskResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
            Should return task with updated info
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateTaskById_validRequestDto_ReturnsUpdatedTask() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setName("Build Payroll Module");
        requestDto.setDescription("Develop salary calculation module with tax deductions "
                + "and automated monthly payslip generation");
        requestDto.setPriority(Priority.HIGH);
        requestDto.setStatus(Status.IN_PROGRESS);
        requestDto.setDueDate(LocalDate.of(2026, 5, 15));
        requestDto.setProjectId(3L);
        requestDto.setAssigneeId(3L);
        requestDto.setLabelId(6L);

        TaskResponseDto expected = TestUtil.BuildPayrollModuleTaskDto();
        expected.setPriority(Priority.HIGH);
        expected.setStatus(Status.IN_PROGRESS);
        expected.setDueDate(LocalDate.of(2026, 5, 15));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/tasks/{id}",
                        2L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        TaskResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), TaskResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return Not Found
           """)
    void updateTaskById_invalidTaskId_ReturnsNotFound() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setName("New Task");
        requestDto.setDescription("New Task description");
        requestDto.setPriority(Priority.LOW);
        requestDto.setStatus(Status.NOT_STARTED);
        requestDto.setDueDate(LocalDate.of(2027, 12, 12));
        requestDto.setProjectId(1L);
        requestDto.setAssigneeId(1L);
        requestDto.setLabelId(2L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/tasks/{id}",
                        10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "john.carter", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing task by its id
           """)
    void deleteTaskById_validRequestDto_Success() throws Exception {
        mockMvc.perform(delete("/tasks/{id}",
                        2L))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "john.carter", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete Task which does not exist in the database (has wrong id)
           """)
    void deleteTaskById_invalidRequestDto_NotFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}",
                        10L))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
                Search tasks by status
                """)
    void search_sameStatus_ReturnsAllTasksWithTheSameStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/tasks/search")
                        .param("statuses", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<TaskResponseDto> actual = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, TaskResponseDto.class)
        );

        assertEquals(2, actual.size());
        assertEquals(Status.IN_PROGRESS, actual.get(0).getStatus());
        assertEquals(Status.IN_PROGRESS, actual.get(1).getStatus());
    }

    @Test
    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
                Search tasks by priority
                """)
    void search_samePriority_ReturnsAllTasksWithTheSamePriority() throws Exception {
        MvcResult result = mockMvc.perform(get("/tasks/search")
                        .param("priorities", "LOW"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<TaskResponseDto> actual = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, TaskResponseDto.class)
        );

        assertEquals(1, actual.size());
        assertEquals(Priority.LOW, actual.get(0).getPriority());
    }
}
