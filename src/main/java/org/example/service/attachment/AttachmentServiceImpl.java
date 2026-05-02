package org.example.service.attachment;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.mapper.AttachmentMapper;
import org.example.model.Attachment;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.attachment.AttachmentRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.example.service.third_party.DropboxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;
    private final DropboxService dropboxService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AttachmentResponseDto createAttachment(Long taskId, MultipartFile file) throws Exception {
        Attachment attachment = new Attachment();
        attachment.setTask(getTaskById(taskId));
        String dropboxPath = dropboxService.uploadFile(file);
        attachment.setDropboxFileId(dropboxPath);
        String resolvedFilename = dropboxPath.substring(dropboxPath.indexOf('_') + 1);
        attachment.setFilename(resolvedFilename);
        attachment.setUploadDate(LocalDateTime.now(ZoneId.of("Europe/Kyiv")));
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public Page<AttachmentResponseDto> getAllForTask(Long taskId, Pageable pageable) {
        getTaskById(taskId);
        return attachmentRepository.findAllByTask_Id(taskId, pageable)
                .map(attachmentMapper::toDto);
    }

    @Override
    @Transactional
    public ResponseEntity<byte[]> retrieveAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));
        Task task = attachment.getTask();
        getTaskById(task.getId());

        byte[] fileBytes = dropboxService.downloadFile(attachment.getDropboxFileId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .body(fileBytes);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Attachment not found: " + id));
        Task task = attachment.getTask();
        getTaskById(task.getId());
        dropboxService.deleteFile(attachment.getDropboxFileId());
        attachmentRepository.delete(attachment);
    }

    private Task getTaskById(Long id) {
        Task task;
        if (!isAdmin()) {
            User user = getCurrentUser();
            task = taskRepository.findTaskByIdAndAssignee(id, user).orElseThrow(
                    () -> new EntityNotFoundException("Task with id " + id + " for user "
                            + user.getId() + " not found")
            );
        } else {
            task = taskRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Task with such id not found: " + id));
        }
        return task;
    }

    private boolean isAdmin() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}
