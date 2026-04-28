package org.example.service.third_party;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.example.model.task.Task;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private static final ZoneId KYIV_ZONE = ZoneId.of("Europe/Kyiv");
    private final GoogleAuthorizationCodeFlow flow;

    private Calendar getCalendarClient(Long userId) throws Exception {
        Credential credential = flow.loadCredential(userId.toString());
        if (credential == null) {
            throw new IllegalStateException("User has not authorized Google Calendar");
        }
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Task Management App")
                .build();
    }

    public String createEvent(Task task, Long currentUserId) throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime endTime = task.getDueDate()
                .atTime(23, 59, 59)
                .atZone(KYIV_ZONE);

        if (startTime.isAfter(endTime)) {
            endTime = startTime.plusMinutes(30);
        }

        Event event = new Event()
                .setSummary(task.getName())
                .setDescription(task.getDescription())
                .setStart(new EventDateTime().setDateTime(toGoogleDateTime(startTime)))
                .setEnd(new EventDateTime().setDateTime(toGoogleDateTime(endTime)))
                .setGuestsCanSeeOtherGuests(true)
                .setStatus("confirmed");

        EventAttendee attendee = new EventAttendee()
                .setEmail(task.getAssignee().getEmail())
                .setDisplayName(task.getAssignee().getFirstName())
                .setResponseStatus("needsAction");
        event.setAttendees(Collections.singletonList(attendee));

        return getCalendarClient(currentUserId).events()
                .insert("primary", event)
                .setSendUpdates("all")
                .setSendNotifications(true)
                .execute()
                .getId();
    }

    public void updateEvent(Task task, Long userId) throws Exception {
        if (task.getCalendarEventId() == null) {
            return;
        }

        Calendar client = getCalendarClient(userId);
        Event event = client.events()
                .get("primary", task.getCalendarEventId())
                .execute();

        event.setSummary(task.getName())
                .setDescription(task.getDescription());

        ZonedDateTime startTime = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime endTime = task.getDueDate().atTime(23, 59, 59).atZone(KYIV_ZONE);

        event.setStart(new EventDateTime().setDateTime(toGoogleDateTime(startTime)));
        event.setEnd(new EventDateTime().setDateTime(toGoogleDateTime(endTime)));
        event.setGuestsCanSeeOtherGuests(true);
        event.setStatus("confirmed");

        EventAttendee attendee = new EventAttendee()
                .setEmail(task.getAssignee().getEmail())
                .setResponseStatus("needsAction");
        event.setAttendees(Collections.singletonList(attendee));

        client.events()
                .update("primary", task.getCalendarEventId(), event)
                .setSendUpdates("all")
                .setSendNotifications(true)
                .execute();
    }

    public void deleteEvent(Task task, Long userId) throws Exception {
        if (task.getCalendarEventId() == null) {
            return;
        }
        getCalendarClient(userId).events()
                .delete("primary", task.getCalendarEventId())
                .setSendUpdates("all")
                .execute();
    }

    private DateTime toGoogleDateTime(ZonedDateTime zdt) {
        return new DateTime(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
