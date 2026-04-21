package org.example.service.comment;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.mapper.CommentMapper;
import org.example.model.Comment;
import org.example.model.task.Task;
import org.example.repository.comment.CommentRepository;
import org.example.repository.task.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskRepository taskRepository;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.save(commentMapper.toEntity(commentRequestDto));
        return commentMapper.toDto(comment);
    }

    @Override
    public Page<CommentResponseDto> getAllForTask(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id " + taskId + " not found")
        );
        List<CommentResponseDto> commentList = commentRepository.findAllByTask(task)
                .stream()
                .map(commentMapper::toDto)
                .toList();
        return new PageImpl<>(commentList, pageable, commentList.size());
    }
}
