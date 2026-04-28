package org.example.service.comment;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTask(getTaskById(commentRequestDto.getTaskId()));
        comment.setText(commentRequestDto.getText());
        comment.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Kyiv")));
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Override
    public Page<CommentResponseDto> getAllForTask(Long taskId, Pageable pageable) {
        getTaskById(taskId);
        return commentRepository.findAllByTask_Id(taskId, pageable)
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id) {
        commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Comment with id " + id + " not found"));
        commentRepository.deleteById(id);
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
