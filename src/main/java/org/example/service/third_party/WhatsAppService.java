package org.example.service.third_party;

import lombok.RequiredArgsConstructor;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final RestTemplate restTemplate;

    @Value("${whatsapp.api.token}")
    private String accessToken;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    public void sendTaskAssignmentWhatsApp(Task task, User assignee) {
        String phone = sanitizePhone(assignee.getPhoneNumber());
        String messageText = buildMessageText(task, assignee);

        String url = apiUrl + "/" + phoneNumberId + "/messages";

        String body = """
                {
                  "messaging_product": "whatsapp",
                  "to": "%s",
                  "type": "text",
                  "text": {
                    "body": "%s"
                  }
                }
                """.formatted(phone, escapeJson(messageText));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("WhatsApp API error: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send WhatsApp message: " + e.getMessage(), e);
        }
    }

    private String buildMessageText(Task task, User assignee) {
        String priority = switch (task.getPriority()) {
            case HIGH -> "🔴 HIGH";
            case MEDIUM -> "🟡 MEDIUM";
            case LOW -> "🟢 LOW";
        };

        String status = switch (task.getStatus()) {
            case NOT_STARTED -> "⏳ NOT_STARTED";
            case IN_PROGRESS -> "\uD83D\uDD04 IN_PROGRESS";
            case COMPLETED -> "✅ COMPLETED";
        };

        return """
                *❗NEW TASK FOR YOU*

                Hello, %s! You have been assigned a task.
                
                *✔️ Task ID: %s*
                *⭐ Name: %s*
                *💡 Project: ```%s``` %s*
                *✍️ Description: %s*
                
                *Priority: %s*
                *Status: %s*
                *Due date: ⏰ %s*

                _✨ Your Task Management System_
                """.formatted(
                assignee.getFirstName(),
                task.getId(),
                task.getName(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getDescription(),
                priority,
                status,
                task.getDueDate()
        );
    }

    private String sanitizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Assignee has no phone number");
        }
        return phone.replaceAll("[^\\d]", "");
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
