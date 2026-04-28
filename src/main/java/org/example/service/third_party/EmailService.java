package org.example.service.third_party;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.model.task.Priority;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.user.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public void sendTaskAssignmentEmail(Task task, User assignee) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(assignee.getEmail());
            helper.setSubject("❗ NEW TASK FOR YOU: " + task.getName());

            String htmlContent = String.format("""
                    <html><body>
                    <p>Hello, %s!</p>
                    <p>You have been assigned a new task.</p>
                    <b><table>
                        <tr><td>Task ID:</td><td>%s</td></tr>
                        <tr><td>Name:</td><td>%s</td></tr>
                        <tr><td>Description:</td><td>%s</td></tr>
                        <tr><td>Priority:</td><td>%s</td></tr>
                        <tr><td>Status:</td><td>%s</td></tr>
                        <tr><td>Due date:</td><td>%s</td></tr>
                        <tr><td>Project:</td><td>%s %s</td></tr>
                    </table></b>
                    <p><i>✨ Your Task Management System ✨</i></p>
                    </body></html>
                    """,
                    assignee.getFirstName(),
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.getProject().getId(),
                    task.getProject().getName()
            );
            String icsContent = generateIcs(task);

            MimeMultipart multipart = new MimeMultipart("alternative");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            MimeBodyPart calendarPart = new MimeBodyPart();
            calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
            calendarPart.setContent(icsContent, "text/calendar; method=REQUEST; charset=utf-8");
            multipart.addBodyPart(calendarPart);

            message.setContent(multipart);

            ByteArrayDataSource icsDataSource = new ByteArrayDataSource(
                    icsContent.getBytes(StandardCharsets.UTF_8),
                    "text/calendar; method=REQUEST"
            );
            helper.addAttachment("task.ics", icsDataSource);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String generateIcs(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String now = LocalDateTime.now().format(formatter);

        String dateStart = task.getDueDate().atStartOfDay().format(formatter);
        String dateEnd = task.getDueDate().atStartOfDay().plusHours(1).format(formatter);

        String description = task.getDescription() != null
                ? task.getDescription().replace("\n", "\\n").replace(",", "\\,")
                : "";

        return "BEGIN:VCALENDAR\r\n"
                + "VERSION:2.0\r\n"
                + "PRODID:-//Task Management System//EN\r\n"
                + "METHOD:REQUEST\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:" + task.getId() + "@task-management-system\r\n"
                + "DTSTAMP:" + now + "\r\n"
                + "DTSTART:" + dateStart + "\r\n"
                + "DTEND:" + dateEnd + "\r\n"
                + "SUMMARY:TASK: " + task.getName() + "\r\n"
                + "DESCRIPTION:" + description + "\r\n"
                + "PRIORITY:" + (task.getPriority() == Priority.HIGH ? "1"
                : task.getPriority() == Priority.MEDIUM ? "5" : "9") + "\r\n"
                + "ORGANIZER;CN=" + getCurrentUser().getUsername()
                + ";RSVP=TRUE:mailto:" + getCurrentUser().getEmail() + "\r\n"
                + "ATTENDEE;RSVP=TRUE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;CN="
                + task.getAssignee().getFirstName() + ":mailto:" + task.getAssignee().getEmail() + "\r\n"
                + "STATUS:CONFIRMED\r\n"
                + "SEQUENCE:0\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}
