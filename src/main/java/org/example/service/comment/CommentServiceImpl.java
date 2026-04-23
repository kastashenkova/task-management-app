package org.example.service.comment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.mapper.CommentMapper;
import org.example.model.Comment;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.comment.CommentRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTask(taskRepository.findById(commentRequestDto.getTaskId())
                .orElseThrow(EntityNotFoundException::new));
        comment.setText(commentRequestDto.getText());
        comment.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Kyiv")));
        commentRepository.save(comment);
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
