package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.service.comment.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @GetMapping()
    @Operation(summary = "Retrieve comments for a task",
            description = "Retrieve comments for a task by its id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Page<CommentResponseDto> getCommentsForTask(@RequestParam Long taskId,
                                                    Pageable pageable) {
        return commentService.getAllForTask(taskId, pageable);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment",
            description = "Delete comment by its id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @commentSecurity.isOwner(authentication, #id))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long id) {
        commentService.deleteCommentById(id);
    }
}
