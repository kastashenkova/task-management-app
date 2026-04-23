package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.service.comment.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PreAuthorize("hasRole('USER')")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve comments for a task",
            description = "Retrieve comments for a task by its id")
    public Page<CommentResponseDto> getCommentsForTask(@RequestParam Long taskId,
                                                    Pageable pageable) {
        return commentService.getAllForTask(taskId, pageable);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment",
            description = "Delete comment by its id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @commentSecurity.isOwner(authentication, #id))")
    public void deleteProjectById(@PathVariable Long id) {
        commentService.deleteCommentById(id);
    }
}
