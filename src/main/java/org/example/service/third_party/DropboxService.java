package org.example.service.third_party;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DropboxService {
    private final WebClient apiClient;
    private final WebClient contentClient;
    private final String token;
    private final ObjectMapper mapper;

    public DropboxService(
            @Qualifier("dropboxApiClient") WebClient apiClient,
            @Qualifier("dropboxContentClient") WebClient contentClient,
            @Value("${dropbox.access-token}") String token
    ) {
        this.apiClient = apiClient;
        this.contentClient = contentClient;
        this.token = token;
        mapper = new ObjectMapper();
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
                .header("Authorization", "Bearer " + token)
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
                    .header("Authorization", "Bearer " + token)
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
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete from Dropbox: " + path, e);
        }
    }
}