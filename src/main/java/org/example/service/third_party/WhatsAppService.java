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

    public void sendTaskAssignmentWhatsApp(Task task, User assignee, String calendarEventUrl) {
        String phone = sanitizePhone(assignee.getPhoneNumber());

        String url = apiUrl + "/" + phoneNumberId + "/messages";

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

        String body = """
        {
          "messaging_product": "whatsapp",
          "to": "%s",
          "type": "template",
          "template": {
            "name": "task_assignment_notification",
            "language": { "code": "en" },
            "components": [
              {
                "type": "body",
                "parameters": [
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" },
                  { "type": "text", "text": "%s" }
                ]
              },
              {
                     "type": "button",
                     "sub_type": "url",
                     "index": "0",
                     "parameters": [
                       { "type": "text", "text": "%s" }
                     ]
              }
            ]
          }
        }
        """.formatted(
                phone,
                escapeJson(assignee.getFirstName()),
                escapeJson(String.valueOf(task.getId())),
                escapeJson(task.getDescription()),
                escapeJson(priority),
                escapeJson(status),
                "⏱\uFE0F " + escapeJson(task.getDueDate().toString()),
                escapeJson(String.valueOf(task.getProject().getId())),
                escapeJson(task.getProject().getName()),
                escapeJson(task.getName()),
                escapeJson(extractEid(calendarEventUrl))
        );

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
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String extractEid(String htmlLink) {
        if (htmlLink == null) {
            return "";
        }
        int index = htmlLink.indexOf("eid=");
        if (index == -1) {
            return "";
        }
        return htmlLink.substring(index + 4);
    }
}
