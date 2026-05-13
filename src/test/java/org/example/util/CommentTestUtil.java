package org.example.util;

import static org.example.util.TaskTestUtil.AddPaymentCountryTask;
import static org.example.util.TaskTestUtil.BuildPayrollModuleTask;
import static org.example.util.UserTestUtil.Alice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.model.Comment;
import org.example.model.task.Task;
import org.example.model.user.User;

public class CommentTestUtil {
    public static final String REFRESH_COMMENT_TEXT = "Please, also add refresh token support before closing this task";
    public static final LocalDateTime REFRESH_COMMENT_TIMESTAMP
            = LocalDate.of(2026, 7, 7).atStartOfDay();

    public static final String CLOUD_COMMENT_TEXT = "Please, provide relevant connection with Google Cloud API";
    public static final LocalDateTime CLOUD_COMMENT_TIMESTAMP
            = LocalDate.of(2026, 8, 8).atStartOfDay();

    public static CommentRequestDto getCommentRequestDto(Long taskId, String text) {
        return new CommentRequestDto()
                .setTaskId(taskId)
                .setText(text);
    }

    public static Comment getComment(Long id, Task task, User user,
                                     String text, LocalDateTime timestamp) {
        return new Comment()
                .setId(id)
                .setTask(task)
                .setUser(user)
                .setText(text)
                .setTimestamp(timestamp);
    }

    public static CommentResponseDto getCommentResponseDto(Long id, Long taskId,
                                                           Long userId, String text,
                                                           LocalDateTime timestamp) {
        return new CommentResponseDto()
                .setId(id)
                .setTaskId(taskId)
                .setUserId(userId)
                .setText(text)
                .setTimestamp(timestamp);
    }

    public static CommentResponseDto AddRefreshTokenCommentDto() {
        return getCommentResponseDto(3L, BuildPayrollModuleTask().getId(),
                Alice().getId(), REFRESH_COMMENT_TEXT, REFRESH_COMMENT_TIMESTAMP);
    }

    public static Comment AddRefreshTokenComment() {
        return getComment(3L, BuildPayrollModuleTask(),
                Alice(), REFRESH_COMMENT_TEXT, REFRESH_COMMENT_TIMESTAMP);
    }

    public static CommentResponseDto AddGoogleCloudAPICommentDto() {
        return getCommentResponseDto(4L, AddPaymentCountryTask().getId(),
                Alice().getId(), CLOUD_COMMENT_TEXT, CLOUD_COMMENT_TIMESTAMP);
    }

    public static Comment AddGoogleCloudAPIComment() {
        return getComment(4L, AddPaymentCountryTask(),
                Alice(), CLOUD_COMMENT_TEXT, CLOUD_COMMENT_TIMESTAMP);
    }
}
