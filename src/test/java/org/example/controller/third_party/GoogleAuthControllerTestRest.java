package org.example.controller.third_party;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import org.example.dto.user.login.UserLoginRequestDto;
import org.example.dto.user.login.UserLoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:database/users/add-roles-to-roles-table.sql",
        "classpath:database/users/add-users-to-users-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/users/delete-users-from-users-table.sql",
        "classpath:database/users/delete-roles-from-roles-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class GoogleAuthControllerTestRest {

    @LocalServerPort
    private int port;

    @MockBean
    private GoogleAuthorizationCodeFlow flow;

    private RestClient restClient;

    @BeforeEach
    public void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api")
                .build();

        GoogleAuthorizationCodeRequestUrl mockUrl =
                mock(GoogleAuthorizationCodeRequestUrl.class);

        when(flow.newAuthorizationUrl()).thenReturn(mockUrl);
        when(mockUrl.setRedirectUri(any())).thenReturn(mockUrl);
        when(mockUrl.setState(any())).thenReturn(mockUrl);
        when(mockUrl.build()).thenReturn("https://accounts.google.com/o/oauth2/auth?mock=true");
    }

    @Test
    @DisplayName("""
                 Returns all authorization link
                 """)
    void authorize_loggedInAdmin_ReturnsAuthorizationLink() {
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

        ResponseEntity<String> response = restClient.get()
                .uri("/auth/google/authorize")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isBlank());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
