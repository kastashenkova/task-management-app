package org.example.service.third_party;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DropboxService {
    private final WebClient apiClient;
    private final WebClient contentClient;
    private final WebClient authClient;
    private final String appKey;
    private final String appSecret;
    private final String refreshToken;
    private final ObjectMapper mapper;

    private String accessToken;
    private Instant tokenExpiresAt = Instant.MIN;

    public DropboxService(
            @Qualifier("dropboxApiClient") WebClient apiClient,
            @Qualifier("dropboxContentClient") WebClient contentClient,
            @Qualifier("dropboxAuthClient") WebClient authClient,
            @Value("${dropbox.app-key}") String appKey,
            @Value("${dropbox.app-secret}") String appSecret,
            @Value("${dropbox.refresh-token}") String refreshToken
    ) {
        this.apiClient = apiClient;
        this.contentClient = contentClient;
        this.authClient = authClient;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.refreshToken = refreshToken;
        this.mapper = new ObjectMapper();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String path = "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        String json = mapper.writeValueAsString(
                Map.of(
                        "path", path,
                        "mode", "overwrite",
                        "autorename", true
                )
        );

        String response = contentClient.post()
                .uri("/2/files/upload")
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Dropbox-API-Arg", json)
                .header("Content-Type", "application/octet-stream")
                .bodyValue(file.getInputStream().readAllBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Map<?, ?> responseMap = mapper.readValue(response, Map.class);
        return (String) responseMap.get("path_display");
    }

    public byte[] downloadFile(String path) {
        try {
            String json = mapper.writeValueAsString(Map.of("path", path));

            return contentClient.post()
                    .uri("/2/files/download")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .header("Dropbox-API-Arg", json)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Failed to download from Dropbox: " + path, e);
        }
    }

    public void deleteFile(String path) {
        try {
            String json = mapper.writeValueAsString(Map.of("path", path));

            apiClient.post()
                    .uri("/2/files/delete_v2")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete from Dropbox: " + path, e);
        }
    }

    private synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiresAt)) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void refreshAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", appKey);
        formData.add("client_secret", appSecret);

        Map<?, ?> response = authClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println("Dropbox token response: " + response);
        System.out.println("expires_in type: " + response.get("expires_in").getClass().getName());
        System.out.println("expires_in value: " + response.get("expires_in"));

        this.accessToken = (String) response.get("access_token");
        long expiresIn = ((Number) response.get("expires_in")).longValue();
        this.tokenExpiresAt = Instant.now().plusSeconds(expiresIn);
    }
}