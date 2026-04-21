package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.service.comment.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comments management",
        description = "Endpoints for managing comments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Add a comment to a task",
            description = "Add a comment to a task")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve tasks for a project",
            description = "Retrieve tasks for a project")
    public Page<CommentResponseDto> getCommentsForTask(@RequestParam Long taskId,
                                                    Pageable pageable) {
        return commentService.getAllForTask(taskId, pageable);
    }
}
