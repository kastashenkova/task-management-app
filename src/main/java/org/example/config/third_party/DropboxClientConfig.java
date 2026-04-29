package org.example.config.third_party;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DropboxClientConfig {

    @Bean("dropboxApiClient")
    public WebClient dropboxApiClient() {
        return WebClient.builder()
                .baseUrl("https://api.dropboxapi.com")
                .build();
    }

    @Bean("dropboxContentClient")
    public WebClient dropboxContentClient() {
        return WebClient.builder()
                .baseUrl("https://content.dropboxapi.com")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(50 * 1024 * 1024))
                .build();
    }
}
