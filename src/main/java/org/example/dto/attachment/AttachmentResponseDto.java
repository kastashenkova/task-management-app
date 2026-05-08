package org.example.dto.attachment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class AttachmentResponseDto {
    private Long id;
    private Long taskId;
    private String dropboxFileId;
    private String filename;
    private LocalDateTime uploadDate;
}
