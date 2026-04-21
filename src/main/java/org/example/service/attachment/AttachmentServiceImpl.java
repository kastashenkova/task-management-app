package org.example.service.attachment;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.attachment.AttachmentRequestDto;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.mapper.AttachmentMapper;
import org.example.model.Attachment;
import org.example.model.task.Task;
import org.example.repository.attachment.AttachmentRepository;
import org.example.repository.task.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;

    @Override
    public AttachmentResponseDto createAttachment(AttachmentRequestDto attachmentRequestDto) {
        Attachment attachment = attachmentRepository.save(attachmentMapper.toEntity(attachmentRequestDto));
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public Page<AttachmentResponseDto> getAllForTask(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id " + taskId + " not found")
        );
        List<AttachmentResponseDto> commentList = attachmentRepository.findAllByTask(task)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
        return new PageImpl<>(commentList, pageable, commentList.size());
    }
}
