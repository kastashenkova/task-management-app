package org.example.service.third_party;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Timeout(5)
public class DropboxServiceTest {

    private MockWebServer authServer;
    private MockWebServer contentServer;
    private MockWebServer apiServer;
    private DropboxService dropboxService;

    @BeforeEach
    void setup() throws IOException {
        authServer = new MockWebServer();
        contentServer = new MockWebServer();
        apiServer = new MockWebServer();

        authServer.start();
        contentServer.start();
        apiServer.start();

        WebClient authClient = WebClient.builder()
                .baseUrl(authServer.url("/").toString())
                .build();

        WebClient contentClient = WebClient.builder()
                .baseUrl(contentServer.url("/").toString())
                .build();

        WebClient apiClient = WebClient.builder()
                .baseUrl(apiServer.url("/").toString())
                .build();

        dropboxService = new DropboxService(
                apiClient,
                contentClient,
                authClient,
                "test-app-key",
                "test-app-secret",
                "test-refresh-token"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        authServer.shutdown();
        contentServer.shutdown();
        apiServer.shutdown();
    }

    @Test
    @DisplayName("""
            Should upload file
            """)
    public void uploadFile_validFile_ReturnsPath() throws IOException, InterruptedException {
        authServer.enqueue(new MockResponse()
                .setBody("{\"access_token\": \"test-token\", \"expires_in\": 3600}")
                .addHeader("Content-Type", "application/json"));

        contentServer.enqueue(new MockResponse()
                .setBody("{\"path_display\": \"/test-uuid_test.pdf\"}")
                .addHeader("Content-Type", "application/json"));

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        String actual = dropboxService.uploadFile(mockFile);

        assertNotNull(actual);
        assertTrue(actual.endsWith("_test.pdf"));

        RecordedRequest authRequest = authServer.takeRequest();
        assertEquals("POST", authRequest.getMethod());
        assertEquals("/oauth2/token", authRequest.getPath());
        String authBody = authRequest.getBody().readUtf8();
        assertTrue(authBody.contains("refresh_token=test-refresh-token"));
        assertTrue(authBody.contains("client_id=test-app-key"));
        assertTrue(authBody.contains("client_secret=test-app-secret"));

        RecordedRequest uploadRequest = contentServer.takeRequest();
        assertEquals("POST", uploadRequest.getMethod());
        assertEquals("/2/files/upload", uploadRequest.getPath());
        assertNotNull(uploadRequest.getHeader("Dropbox-API-Arg"));
        assertEquals("Bearer test-token", uploadRequest.getHeader("Authorization"));
    }

    @Test
    @DisplayName("""
            Should download file
            """)
    public void downloadFile_existingFile_ReturnsFile() throws InterruptedException {
        authServer.enqueue(new MockResponse()
                .setBody("{\"access_token\": \"test-token\", \"expires_in\": 3600}")
                .addHeader("Content-Type", "application/json"));

        contentServer.enqueue(new MockResponse()
                .setBody("{\"path_display\": \"/test-uuid_test.pdf\"}")
                .addHeader("Content-Type", "application/json"));

        byte[] actual = dropboxService.downloadFile("/test-uuid_test.pdf");

        assertNotNull(actual);

        RecordedRequest authRequest = authServer.takeRequest();
        assertEquals("POST", authRequest.getMethod());
        assertEquals("/oauth2/token", authRequest.getPath());
        String authBody = authRequest.getBody().readUtf8();
        assertTrue(authBody.contains("refresh_token=test-refresh-token"));
        assertTrue(authBody.contains("client_id=test-app-key"));
        assertTrue(authBody.contains("client_secret=test-app-secret"));

        RecordedRequest downloadRequest = contentServer.takeRequest();
        assertEquals("POST", downloadRequest.getMethod());
        assertEquals("/2/files/download", downloadRequest.getPath());
        assertNotNull(downloadRequest.getHeader("Dropbox-API-Arg"));
        assertEquals("Bearer test-token", downloadRequest.getHeader("Authorization"));
    }

    @Test
    @DisplayName("""
            Should return Runtime Exception
            """)
    public void downloadFile_nonExistingFile_ReturnsRuntimeException() {
        authServer.enqueue(new MockResponse()
                .setBody("{\"access_token\": \"test-token\", \"expires_in\": 3600}")
                .addHeader("Content-Type", "application/json"));

        contentServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\": \"not_found\"}"));

        assertThrows(RuntimeException.class,
                () -> dropboxService.downloadFile("/test-uuid_test.pdf"));
    }

    @Test
    @DisplayName("""
            Should delete existing file
            """)
    public void deleteFile_existingFile_Success() throws InterruptedException {
        authServer.enqueue(new MockResponse()
                .setBody("{\"access_token\": \"test-token\", \"expires_in\": 3600}")
                .addHeader("Content-Type", "application/json"));

        apiServer.enqueue(new MockResponse()
                .setBody("{\"metadata\": {}}")
                .addHeader("Content-Type", "application/json"));

        dropboxService.deleteFile("/test-uuid_test.pdf");

        RecordedRequest authRequest = authServer.takeRequest();
        assertEquals("POST", authRequest.getMethod());
        assertEquals("/oauth2/token", authRequest.getPath());
        String authBody = authRequest.getBody().readUtf8();
        assertTrue(authBody.contains("refresh_token=test-refresh-token"));
        assertTrue(authBody.contains("client_id=test-app-key"));
        assertTrue(authBody.contains("client_secret=test-app-secret"));

        RecordedRequest deleteRequest = apiServer.takeRequest();
        assertEquals("POST", deleteRequest.getMethod());
        assertEquals("/2/files/delete_v2", deleteRequest.getPath());
        assertEquals("Bearer test-token", deleteRequest.getHeader("Authorization"));
    }

    @Test
    @DisplayName("""
            Should return Runtime Exception
            """)
    public void deleteFile_nonExistingFile_ReturnsRuntimeException() {
        authServer.enqueue(new MockResponse()
                .setBody("{\"access_token\": \"test-token\", \"expires_in\": 3600}")
                .addHeader("Content-Type", "application/json"));

        apiServer.enqueue(new MockResponse()
                .setResponseCode(409)
                .setBody("{\"error\": \"path_not_found\"}"));

        assertThrows(RuntimeException.class,
                () -> dropboxService.deleteFile("/test-uuid_test.pdf"));
    }
}