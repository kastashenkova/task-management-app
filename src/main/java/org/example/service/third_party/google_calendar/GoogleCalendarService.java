package org.example.service.third_party.google_calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.example.model.task.Task;
import org.example.service.third_party.CalendarEventResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private static final ZoneId KYIV_ZONE = ZoneId.of("Europe/Kyiv");

    private final GoogleCalendarClientFactory calendarClientFactory;

    public CalendarEventResult createEvent(Task task, Long adminId) throws Exception {
        Calendar client = calendarClientFactory.getClient(adminId);

        ZonedDateTime start = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime end = task.getDueDate()
                .atTime(23, 59)
                .atZone(KYIV_ZONE);

        if (start.isAfter(end)) {
            end = start.plusWeeks(1);
        }

        Event event = new Event()
                .setSummary("\uD83C\uDFAF " + task.getName())
                .setDescription(buildDescription(task))
                .setStart(toEventDateTime(start))
                .setEnd(toEventDateTime(end))
                .setStatus("confirmed")
                .setColorId(String.valueOf(task.getLabel().getId()))
                .setGuestsCanSeeOtherGuests(true)
                .setGuestsCanModify(false)
                .setGuestsCanInviteOthers(false);

        EventAttendee attendee = new EventAttendee()
                .setEmail(task.getAssignee().getEmail())
                .setDisplayName(task.getAssignee().getFirstName())
                .setResponseStatus("needsAction");

        event.setAttendees(Collections.singletonList(attendee));

        Event created = client.events()
                .insert("primary", event)
                .setSendUpdates("all")
                .execute();

        return new CalendarEventResult(created.getId(), created.getHtmlLink());
    }

    public void updateEvent(Task task, Long adminId) throws Exception {
        if (task.getCalendarEventId() == null) {
            return;
        }

        Calendar client = calendarClientFactory.getClient(adminId);

        Event event = client.events()
                .get("primary", task.getCalendarEventId())
                .execute();

        ZonedDateTime start = ZonedDateTime.now(KYIV_ZONE);
        ZonedDateTime end = task.getDueDate()
                .atTime(23, 59)
                .atZone(KYIV_ZONE);

        event.setSummary("\uD83C\uDFAF " + task.getName());
        event.setDescription(buildDescription(task));
        event.setStart(toEventDateTime(start));
        event.setEnd(toEventDateTime(end));

        EventAttendee attendee = new EventAttendee()
                .setEmail(task.getAssignee().getEmail())
                .setResponseStatus("needsAction");

        event.setAttendees(Collections.singletonList(attendee));

        client.events()
                .update("primary", task.getCalendarEventId(), event)
                .setSendUpdates("all")
                .execute();
    }

    public void deleteEvent(Task task, Long adminId) throws Exception {
        if (task.getCalendarEventId() == null) {
            return;
        }

        calendarClientFactory.getClient(adminId)
                .events()
                .delete("primary", task.getCalendarEventId())
                .setSendUpdates("all")
                .execute();
    }

    private EventDateTime toEventDateTime(ZonedDateTime zdt) {
        return new EventDateTime()
                .setDateTime(new DateTime(zdt.toInstant().toEpochMilli()))
                .setTimeZone(KYIV_ZONE.toString());
    }

    private String buildDescription(Task task) {
        return String.format("""
                Task ID: %s
                Description: %s
                Priority: %s
                Status: %s
                Project: %s %s
                """,
                task.getId(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getProject().getId(),
                task.getProject().getName()
        );
    }
}
