package org.example.service.attachment;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.mapper.AttachmentMapper;
import org.example.model.Attachment;
import org.example.model.task.Task;
import org.example.repository.attachment.AttachmentRepository;
import org.example.repository.task.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;
    private final DropboxService dropboxService;

    @Override
    public AttachmentResponseDto createAttachment(Long taskId, MultipartFile file) throws Exception {
        Attachment attachment = new Attachment();
        attachment.setTask(taskRepository.findById(taskId)
                .orElseThrow(EntityNotFoundException::new));
        String dropboxPath = dropboxService.uploadFile(file);
        attachment.setDropboxFileId(dropboxPath);
        String resolvedFilename = dropboxPath.substring(dropboxPath.indexOf('_') + 1);
        attachment.setFilename(resolvedFilename);
        attachment.setUploadDate(LocalDateTime.now(ZoneId.of("Europe/Kyiv")));
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public Page<AttachmentResponseDto> getAllForTask(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(EntityNotFoundException::new);
        return attachmentRepository.findAllByTask(task, pageable)
                .map(attachmentMapper::toDto);
    }

    public ResponseEntity<byte[]> retrieveAttachment(Long id) {

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));

        byte[] fileBytes = dropboxService.downloadFile(attachment.getDropboxFileId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .body(fileBytes);
    }

    @Override
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Attachment not found: " + id));
        dropboxService.deleteFile(attachment.getDropboxFileId());
        attachmentRepository.delete(attachment);
    }
}
