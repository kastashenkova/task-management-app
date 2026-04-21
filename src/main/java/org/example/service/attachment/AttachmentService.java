package org.example.service.attachment;

import org.example.dto.attachment.AttachmentRequestDto;
import org.example.dto.attachment.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttachmentService {
    AttachmentResponseDto createAttachment(AttachmentRequestDto attachmentRequestDto);

    Page<AttachmentResponseDto> getAllForTask(Long taskId, Pageable pageable);
}
