package org.example.service.third_party;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.example.dto.task.TaskResponseDto;
import org.example.model.label.Label;
import org.example.model.project.Project;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.service.third_party.google_calendar.GoogleCalendarClientFactory;
import org.example.service.third_party.google_calendar.GoogleCalendarService;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {

    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    @Mock
    private GoogleCalendarClientFactory calendarClientFactory;

    @Mock
    private Calendar mockCalendar;

    @Test
    @DisplayName("""
            Should return created calendar event link
            """)
    public void createEvent_validRequest_ReturnsEventLink() throws Exception {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = new User()
                .setId(addPaymentCountryTaskDto.getAssigneeId())
                .setUsername("john.carter")
                .setEmail("john.carter@company.com")
                .setPhoneNumber("+380988888888")
                .setFirstName("John")
                .setLastName("Carter");
        Project mockProject = new Project()
                .setId(addPaymentCountryTaskDto.getProjectId())
                .setName("mockProject");
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockAssignee)
                .setLabel(mockLabel)
                .setCalendarEventId("test-calendar-event-id");

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Insert mockInsert = mock(Calendar.Events.Insert.class);

        when(calendarClientFactory.getClient(mockAssignee.getId())).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);
        when(mockEvents.insert(eq("primary"), any(Event.class))).thenReturn(mockInsert);
        when(mockInsert.setSendUpdates("all")).thenReturn(mockInsert);
        Event mockCreatedEvent = new Event()
                .setId("created-event-id")
                .setHtmlLink("https://calendar.google.com/event/123");
        when(mockInsert.execute()).thenReturn(mockCreatedEvent);

        googleCalendarService.createEvent(mockTask, mockAssignee.getId());

        verify(calendarClientFactory, times(1))
                .getClient(mockAssignee.getId());
        verify(mockInsert, times(1)).execute();
    }

    @Test
    @DisplayName("""
            Should update existing Google Calendar event
            """)
    public void updateEvent_existingEvent_Success() throws Exception {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = new User()
                .setId(addPaymentCountryTaskDto.getAssigneeId())
                .setUsername("john.carter")
                .setEmail("john.carter@company.com")
                .setPhoneNumber("+380988888888")
                .setFirstName("John")
                .setLastName("Carter");
        Project mockProject = new Project()
                .setId(addPaymentCountryTaskDto.getProjectId())
                .setName("mockProject");
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockAssignee)
                .setLabel(mockLabel)
                .setCalendarEventId("test-calendar-event-id");

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Get mockGet = mock(Calendar.Events.Get.class);
        Event mockExistingEvent = new Event();

        when(mockEvents.get("primary", mockTask.getCalendarEventId())).thenReturn(mockGet);
        when(mockGet.execute()).thenReturn(mockExistingEvent);

        when(calendarClientFactory.getClient(mockAssignee.getId())).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);

        Calendar.Events.Update mockUpdate = mock(Calendar.Events.Update.class);
        when(mockEvents.update(eq("primary"),
                eq(mockTask.getCalendarEventId()),
                any(Event.class)))
                .thenReturn(mockUpdate);
        when(mockUpdate.setSendUpdates("all")).thenReturn(mockUpdate);
        when(mockUpdate.execute()).thenReturn(new Event());

        googleCalendarService.updateEvent(mockTask, mockAssignee.getId());

        verify(calendarClientFactory, times(1))
                .getClient(mockAssignee.getId());
        verify(mockUpdate, times(1)).execute();
    }

    @Test
    @DisplayName("""
            Should skip updating calendar event
            """)
    public void updateEvent_nullCalendarEventId_SkipsGoogleApiCall() throws Exception {
        Task mockTask = new Task()
                .setCalendarEventId(null);

        googleCalendarService.updateEvent(mockTask, 1L);

        verify(calendarClientFactory, times(0))
                .getClient(anyLong());
    }

    @Test
    @DisplayName("""
            Should delete existing task event
            """)
    public void deleteEvent_existingEvent_Success() throws Exception {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = new User()
                .setId(addPaymentCountryTaskDto.getAssigneeId())
                .setUsername("john.carter")
                .setEmail("john.carter@company.com")
                .setPhoneNumber("+380988888888")
                .setFirstName("John")
                .setLastName("Carter");
        Project mockProject = new Project()
                .setId(addPaymentCountryTaskDto.getProjectId())
                .setName("mockProject");
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = new Task()
                .setId(addPaymentCountryTaskDto.getId())
                .setName(addPaymentCountryTaskDto.getName())
                .setDescription(addPaymentCountryTaskDto.getDescription())
                .setPriority(addPaymentCountryTaskDto.getPriority())
                .setStatus(addPaymentCountryTaskDto.getStatus())
                .setDueDate(addPaymentCountryTaskDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockAssignee)
                .setLabel(mockLabel)
                .setCalendarEventId("test-calendar-event-id");

        Calendar.Events mockEvents = mock(Calendar.Events.class);
        Calendar.Events.Delete mockDelete = mock(Calendar.Events.Delete.class);

        when(calendarClientFactory.getClient(mockAssignee.getId())).thenReturn(mockCalendar);
        when(mockCalendar.events()).thenReturn(mockEvents);
        when(mockEvents.delete("primary", mockTask.getCalendarEventId()))
                .thenReturn(mockDelete);
        when(mockDelete.setSendUpdates("all")).thenReturn(mockDelete);
        doNothing().when(mockDelete).execute();

        googleCalendarService.deleteEvent(mockTask, mockAssignee.getId());

        verify(calendarClientFactory, times(1))
                .getClient(mockAssignee.getId());
        verify(mockDelete, times(1)).execute();
    }

    @Test
    @DisplayName("""
            Should skip deleting calendar event
            """)
    public void deleteEvent_nullCalendarEventId_SkipsGoogleApiCall() throws Exception {
        Task mockTask = new Task()
                .setCalendarEventId(null);

        googleCalendarService.deleteEvent(mockTask, 1L);

        verify(calendarClientFactory, times(0))
                .getClient(anyLong());
    }
}
