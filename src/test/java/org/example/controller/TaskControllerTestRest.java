package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import org.example.dto.task.TaskResponseDto;
import org.example.dto.user.login.UserLoginRequestDto;
import org.example.dto.user.login.UserLoginResponseDto;
import org.example.util.RestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:database/projects/add-projects-to-projects-table.sql",
        "classpath:database/users/add-roles-to-roles-table.sql",
        "classpath:database/users/add-users-to-users-table.sql",
        "classpath:database/tasks/add-labels-to-labels-table.sql",
        "classpath:database/tasks/add-tasks-to-tasks-table.sql"
},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
        "classpath:database/tasks/delete-labels-from-labels-table.sql",
        "classpath:database/projects/delete-projects-from-projects-table.sql",
        "classpath:database/users/delete-users-from-users-table.sql",
        "classpath:database/users/delete-roles-from-roles-table.sql",
},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TaskControllerTestRest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    public void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api")
                .build();
    }

    @Test
    @DisplayName("""
                 Search tasks by priority
                 """)
    void search_samePriority_ReturnsAllTasksWithTheSamePriority() {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setUsername("john.carter");
        loginRequest.setPassword("admin123");

        UserLoginResponseDto loginResponse = restClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .retrieve()
                .body(UserLoginResponseDto.class);

        assertNotNull(loginResponse);
        String token = loginResponse.token();

        ResponseEntity<RestPage<TaskResponseDto>> response = restClient.get()
                .uri("/tasks/search?priorities=HIGH")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(2, Objects.requireNonNull(response.getBody()).getContent().size());
    }
}
