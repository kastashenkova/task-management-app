package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.attachment.AttachmentRequestDto;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.service.attachment.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Attachments management",
        description = "Endpoints for managing attachments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    @Operation(summary = "Upload an attachment to a task",
            description = "File gets uploaded to Dropbox and we store the Dropbox File ID in our database")
    public AttachmentResponseDto createAttachment(@RequestBody AttachmentRequestDto attachmentRequestDto) {
        return attachmentService.createAttachment(attachmentRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve attachments for a task",
            description = "Get the Dropbox File ID from the database and retrieve the actual file from Dropbox")
    public Page<AttachmentResponseDto> getAttachmentsForTask(@RequestParam Long taskId,
                                                          Pageable pageable) {
        return attachmentService.getAllForTask(taskId, pageable);
    }
}
