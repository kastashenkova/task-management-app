package org.example.dto.attachment;

import lombok.Getter;
import lombok.Setter;
import org.example.model.task.Task;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttachmentResponseDto {
    private Long id;
    private Task task;
    private String dropboxFileId;
    private String filename;
    private LocalDateTime uploadDate;
}
