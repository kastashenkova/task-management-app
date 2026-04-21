package org.example.dto.attachment;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class AttachmentRequestDto {
    @NotEmpty
    private Long taskId;
    @NotBlank
    @Length(min = 1, max = 100)
    private String dropboxFileId;
    @NotBlank
    @Length(min = 1, max = 255)
    private String filename;
    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime uploadDate;
}
