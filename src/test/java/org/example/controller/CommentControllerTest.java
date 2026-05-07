package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.dto.task.TaskResponseDto;
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
public class CommentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available comments within the specific task
           """)
    void getCommentsForTask_twoCommentsWithinTask_ReturnsTwoComments() throws Exception {
        MvcResult result = mockMvc.perform(get("/comments?taskId=2"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<CommentResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, CommentResponseDto.class)
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
           Should return empty list
           """)
    void getCommentsForTask_noCommentsWithinTask_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/comments?taskId=5"))
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
    @DisplayName("""
           Create a new Comment within the specific task
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
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createComment_validRequestDto_ReturnsCreatedComment() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setTaskId(2L);
        requestDto.setText("Please, also add refresh token support before closing this task");

        CommentResponseDto expected = TestUtil.AddRefreshTokenCommentDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/comments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CommentResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CommentResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id",
                        "timestamp",
                        "userId")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing comment by its id
           """)
    void deleteCommentById_existingComment_Success() throws Exception {
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return Not Found
           """)
    void deleteCommentById_nonExistingComment_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/comments/10"))
                .andExpect(status().isNotFound());
    }
}
