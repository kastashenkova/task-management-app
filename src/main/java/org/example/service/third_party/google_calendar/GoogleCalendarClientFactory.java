package org.example.service.third_party.google_calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleCalendarClientFactory {
    private static final String APP_NAME = "Task Management App";

    private final GoogleAuthorizationCodeFlow flow;

    public Calendar getClient(Long adminId) throws Exception {
        Credential credential = flow.loadCredential(adminId.toString());
        if (credential == null) {
            throw new IllegalStateException("Admin has not connected Google Calendar: " + adminId);
        }
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential
        ).setApplicationName(APP_NAME).build();
    }
}
