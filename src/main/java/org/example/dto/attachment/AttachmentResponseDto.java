package org.example.dto.attachment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttachmentResponseDto {
    private Long id;
    private Long taskId;
    private String dropboxFileId;
    private String filename;
    private LocalDateTime uploadDate;
}
