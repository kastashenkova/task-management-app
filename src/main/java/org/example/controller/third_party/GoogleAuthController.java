package org.example.controller.third_party;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
@Tag(name = "Google authentication management",
        description = "Endpoints for managing ADMIN authentification to create tasks in Google Calendar")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleAuthorizationCodeFlow flow;
    @Value("${google.redirect.uri}")
    private String redirectUri;

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String userId = user.getId().toString();

        String url = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(userId)
                .build();

        return ResponseEntity.ok(url);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code,
                                           @RequestParam String state) throws Exception {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        flow.createAndStoreCredential(tokenResponse, state);
        return ResponseEntity.ok("Google Calendar connected");
    }
}
