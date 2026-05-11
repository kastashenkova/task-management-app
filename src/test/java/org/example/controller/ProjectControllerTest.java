package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.model.project.Status;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available projects
           """)
    void getProjects_fiveProjects_ReturnsAllFiveProjects() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<ProjectResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, ProjectResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(5, actualList.size());
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return empty list
           """)
    void getProjects_noProjects_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<ProjectResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, ProjectResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return specific project by its id
           """)
    void getProjectById_secondProject_ReturnsTheSecondProjectInDto() throws Exception {
        ProjectResponseDto expected = TestUtil.MobileBankingAppProjectDto();

        MvcResult result = mockMvc.perform(get("/projects/{id}",
                        2L))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        ProjectResponseDto actual = objectMapper.readValue(
                json,
                ProjectResponseDto.class
        );

        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return Not Found
           """)
    void getProjectById_invalidProjectId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/projects/{id}",
                        10L))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("""
           Create a new Project
           """)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createProject_validRequestDto_ReturnsCreatedProject() throws Exception {
        ProjectRequestDto requestDto = new ProjectRequestDto();
        requestDto.setName("Payment");
        requestDto.setDescription("Develop your own PayPal system");
        requestDto.setStartDate(LocalDate.of(2026, 5, 6));
        requestDto.setEndDate(LocalDate.of(2026, 7, 8));
        requestDto.setStatus(Status.INITIATED);

        ProjectResponseDto expected = TestUtil.PayPalProjectDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/projects")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProjectResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ProjectResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("""
            Should return project with updated info
            """)
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateProjectById_validRequestDto_ReturnsUpdatedProject() throws Exception {
        ProjectRequestDto requestDto = new ProjectRequestDto();
        requestDto.setName("Mobile Banking App");
        requestDto.setDescription("Mobile application for managing bank accounts, transactions and money transfers");
        requestDto.setStartDate(LocalDate.of(2026, 5, 6));
        requestDto.setEndDate(LocalDate.of(2026, 8, 9));
        requestDto.setStatus(Status.IN_PROGRESS);

        ProjectResponseDto expected = TestUtil.MobileBankingAppProjectDto();
        expected.setStartDate(LocalDate.of(2026, 5, 6));
        expected.setEndDate(LocalDate.of(2026, 8, 9));
        expected.setStatus(Status.IN_PROGRESS);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/projects/{id}",
                2L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ProjectResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ProjectResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return Not Found
           """)
    void updateProjectById_invalidProjectId_ReturnsNotFound() throws Exception {
        ProjectRequestDto requestDto = new ProjectRequestDto();
        requestDto.setName("New Project");
        requestDto.setDescription("New Project description");
        requestDto.setStartDate(LocalDate.of(2026, 3, 2));
        requestDto.setEndDate(LocalDate.of(2026, 12, 3));
        requestDto.setStatus(Status.IN_PROGRESS);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/projects/{id}",
                        10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing project by its id
           """)
    void deleteProjectById_validRequestDto_Success() throws Exception {
        mockMvc.perform(delete("/projects/{id}",
                        2L))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Delete Project which does not exist in the database (has wrong id)
           """)
    void deleteProjectById_invalidRequestDto_NotFound() throws Exception {
        mockMvc.perform(delete("/projects/{id}",
                        10L))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
                Search projects by status
                """)
    void search_sameStatus_ReturnsAllProjectsWithTheSameStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects/search")
                        .param("statuses", "COMPLETED"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<ProjectResponseDto> actual = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProjectResponseDto.class)
        );

        assertEquals(2, actual.size());
        assertEquals(Status.COMPLETED, actual.get(0).getStatus());
        assertEquals(Status.COMPLETED, actual.get(1).getStatus());
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
                Search projects by status
                """)
    void search_sameEndDate_ReturnsAllProjectsWithTheSameEndDate() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects/search")
                        .param("endDates", "2026-12-31"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<ProjectResponseDto> actual = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProjectResponseDto.class)
        );

        assertEquals(2, actual.size());
        assertEquals(LocalDate.of(2026, 12, 31), actual.get(0).getEndDate());
        assertEquals(LocalDate.of(2026, 12, 31), actual.get(1).getEndDate());
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @DisplayName("""
                Search projects by end date which does not exist in the database
                """)
    void search_notExistingEndDate_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/projects/search")
                        .param("endDates", "2026-11-11"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<ProjectResponseDto> actual = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProjectResponseDto.class)
        );

        assertEquals(0, actual.size());
    }
}
