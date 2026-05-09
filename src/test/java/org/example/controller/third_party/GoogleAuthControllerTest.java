package org.example.controller.third_party;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
public class GoogleAuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean // for testing interaction with the external API
    private GoogleAuthorizationCodeFlow flow;

    @WithUserDetails("john.carter")
    @Test
    @DisplayName("""
            Should return authorization link
            """)
    public void authorize_loggedInAdmin_ReturnsAuthorizationLink() throws Exception {
        GoogleAuthorizationCodeRequestUrl mockUrl =
                mock(GoogleAuthorizationCodeRequestUrl.class);

        when(flow.newAuthorizationUrl()).thenReturn(mockUrl);
        when(mockUrl.setRedirectUri(any())).thenReturn(mockUrl);
        when(mockUrl.setState(any())).thenReturn(mockUrl);
        when(mockUrl.build()).thenReturn("https://accounts.google.com/o/oauth2/auth?mock=true");

        mockMvc.perform(get("/auth/google/authorize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @WithUserDetails("david.nguyen")
    @Test
    @DisplayName("""
            Should return Forbidden exception
            """)
    public void authorize_loggedInUser_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/auth/google/authorize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("""
                Should process Google OAuth2 callback and confirm connection
                """)
    public void callback_validCodeAndState_Success() throws Exception {
        GoogleAuthorizationCodeTokenRequest mockTokenRequest =
                mock(GoogleAuthorizationCodeTokenRequest.class);
        GoogleTokenResponse mockTokenResponse = mock(GoogleTokenResponse.class);

        when(flow.newTokenRequest("mock-auth-code")).thenReturn(mockTokenRequest);
        when(mockTokenRequest.setRedirectUri(any())).thenReturn(mockTokenRequest);
        when(mockTokenRequest.execute()).thenReturn(mockTokenResponse);
        when(flow.createAndStoreCredential(eq(mockTokenResponse), eq("1")))
                .thenReturn(null);

        mockMvc.perform(get("/auth/google/callback")
                        .param("code", "mock-auth-code")
                        .param("state", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
                Should return IOException
                """)
    public void callback_invalidCodeAndState_ReturnsIOException() throws Exception {
        GoogleAuthorizationCodeTokenRequest mockTokenRequest =
                mock(GoogleAuthorizationCodeTokenRequest.class);

        when(flow.newTokenRequest("invalid-code")).thenReturn(mockTokenRequest);
        when(mockTokenRequest.setRedirectUri(any())).thenReturn(mockTokenRequest);
        when(mockTokenRequest.execute()).thenThrow(new IOException("Invalid code"));

        mockMvc.perform(get("/auth/google/callback")
                        .param("code", "invalid-code")
                        .param("state", "1"))
                .andExpect(status().isInternalServerError());
    }
}
