package org.example.service.attachment;

import org.example.dto.attachment.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentResponseDto createAttachment(Long taskId, MultipartFile file) throws Exception;

    Page<AttachmentResponseDto> getAllForTask(Long taskId, Pageable pageable);

    ResponseEntity<byte[]> retrieveAttachment(Long id) throws Exception;

    void deleteAttachment(Long id) throws Exception;
}
