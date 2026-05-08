package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.user.login.UserLoginRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("""
            Should return registered user info
            """)
    @Sql(scripts = {
            "classpath:database/users/add-roles-to-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_validRequest_ReturnsCreatedUser() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setUsername("alice.black");
        requestDto.setEmail("alice.black@company.com");
        requestDto.setPhoneNumber("+380912345678");
        requestDto.setFirstName("Alice");
        requestDto.setLastName("Black");
        requestDto.setPassword("password");
        requestDto.setRepeatPassword("password");

        UserResponseDto expected = TestUtil.AliceDto();

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), UserResponseDto.class);

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @Test
    @DisplayName("""
            Should return registration exception
            """)
    @Sql(scripts = {
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_userWithExistingUsername_ReturnsRegistrationException() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setUsername("john.carter");
        requestDto.setEmail("john.carter@company.com");
        requestDto.setPhoneNumber("+380988888888");
        requestDto.setFirstName("John");
        requestDto.setLastName("Carter");
        requestDto.setPassword("password");
        requestDto.setRepeatPassword("password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Should return JWT token
            """)
    void login_existingUser_ReturnsToken() throws Exception {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setUsername("john.carter");
        loginRequest.setPassword("admin123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("""
            Should return UNAUTHORIZED exception 
            """)
    void login_nonExistingUser_ReturnsUnauthorized() throws Exception {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setUsername("non-existing-user");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
