package org.example.dto.comment;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class CommentRequestDto {
    @NotEmpty
    private Long taskId;
    @NotEmpty
    private Long userId;
    @NotBlank
    @Length(min = 1, max = 500)
    private String text;
    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime timestamp;
}
