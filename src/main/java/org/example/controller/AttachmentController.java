package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.service.attachment.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachments management",
        description = "Endpoints for managing attachments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an attachment to a task",
            description = "File gets uploaded to Dropbox and we store the Dropbox File ID in our database")
    @PreAuthorize("hasRole('ADMIN')")
    public AttachmentResponseDto createAttachment(@RequestParam Long taskId,
                                                  @RequestParam MultipartFile file)
            throws Exception {
        return attachmentService.createAttachment(taskId, file);
    }

    @GetMapping()
    @Operation(summary = "Retrieve attachments for a task",
            description = "Get the Dropbox File ID from the database and retrieve the actual file from Dropbox")
    public Page<AttachmentResponseDto> getAttachmentsForTask(@RequestParam Long taskId,
                                                          Pageable pageable) {
        return attachmentService.getAllForTask(taskId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve attachment",
            description = "Retrieve attachment by its id")
    public ResponseEntity<byte[]> retrieveAttachment(@PathVariable Long id)
            throws Exception {
        return attachmentService.retrieveAttachment(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attachment",
            description = "Delete attachment by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAttachment(@PathVariable Long id) throws Exception {
        attachmentService.deleteAttachment(id);
    }
}
