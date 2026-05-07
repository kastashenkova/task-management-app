package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.user.RoleDto;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserResponseDto;
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
public class UsersControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("""
            Should return user with updated role
            """)
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
    void updateUserRoleById_validRequestDto_ReturnsUpdatedUser() throws Exception {
        RoleDto requestDto = new RoleDto();
        requestDto.setName("ADMIN");

        UserResponseDto expected = TestUtil.SarahMitchellDto();
        expected.setRole("ADMIN");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/{id}/role", 2L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    @Test
    @DisplayName("""
           Update role of the User which does not exist in the database (has wrong id)
           """)
    void updateUserRoleById_invalidRequestDto_NotFound() throws Exception {
        RoleDto requestDto = new RoleDto();
        requestDto.setName("ADMIN");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/users/{id}/role", 10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "john.carter", roles = {"ADMIN"})
    @Test
    @DisplayName("""
                Should return current admin profile info
                """)
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
    void getMyInfo_adminUser_ReturnsInfoOfTheLoggedInUser() throws Exception {
        UserResponseDto expected = TestUtil.JohnDto();

        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        UserResponseDto actual = objectMapper.readValue(
                json,
                UserResponseDto.class
        );

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "sarah.mitchell")
    @Test
    @DisplayName("""
                Should return current admin profile info
                """)
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
    void getMyInfo_userRoleUser_ReturnsInfoOfTheLoggedInUser() throws Exception {
        UserResponseDto expected = TestUtil.SarahMitchellDto();

        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        UserResponseDto actual = objectMapper.readValue(
                json,
                UserResponseDto.class
        );

        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "sarah.mitchell")
    @Test
    @DisplayName("""
            Should return user with updated email and last name
            """)
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
    void updateMyInfo_emailAndLastNameRequestDto_ReturnsUpdatedUser() throws Exception {
        UserUpdateRequestDto userRequestDto = new UserUpdateRequestDto();
        userRequestDto.setEmail("sarah.white@company.com");
        userRequestDto.setLastName("White");

        UserResponseDto expected = TestUtil.SarahWhiteDto();

        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "sarah.mitchell")
    @Test
    @DisplayName("""
            Should return user with updated info
            """)
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
    void updateMyInfo_fullRequestDto_ReturnsUpdatedUser() throws Exception {
        UserUpdateRequestDto userRequestDto = new UserUpdateRequestDto();
        userRequestDto.setUsername("alice.black");
        userRequestDto.setEmail("alice.black@company.com");
        userRequestDto.setPhoneNumber("+380912345678");
        userRequestDto.setFirstName("Alice");
        userRequestDto.setLastName("Black");
        userRequestDto.setPassword("new-password");
        userRequestDto.setRepeatPassword("new-password");

        UserResponseDto expected = TestUtil.AliceDto();

        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "sarah.mitchell")
    @Test
    @DisplayName("""
            Should return bad request
            """)
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
    void updateMyInfo_passwordNotMatchRepeatPassword_ReturnsBadRequest() throws Exception {
        UserUpdateRequestDto userRequestDto = new UserUpdateRequestDto();
        userRequestDto.setPassword("new-password");
        userRequestDto.setRepeatPassword("other-new-password");

        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
