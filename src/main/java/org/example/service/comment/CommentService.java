package org.example.service.comment;

import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

public interface CommentService {

    CommentResponseDto createComment(CommentRequestDto commentRequestDto);

    Page<CommentResponseDto> getAllForTask(Long taskId, Pageable pageable);

    void deleteCommentById(Long id);
}
