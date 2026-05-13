package org.example.service.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.mapper.CommentMapper;
import org.example.model.Comment;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.comment.CommentRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.example.util.AttachmentTestUtil;
import org.example.util.CommentTestUtil;
import org.example.util.UserTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("""
                Should create a new Comment
                """)
    void createComment_newComment_ReturnsNewComment() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto()
                .setTaskId(3L)
                .setText("Please, also add refresh token support before closing this task");

        Task task = new Task()
                .setId(commentRequestDto.getTaskId());

        Comment commentWithoutId = new Comment()
                .setTask(task)
                .setText(commentRequestDto.getText());

        Comment saved = new Comment()
                .setId(1L)
                .setTask(task)
                .setText(commentWithoutId.getText());

        CommentResponseDto expected = CommentTestUtil.AddRefreshTokenCommentDto();
        expected.setId(1L);

        UserResponseDto userResponseDto = UserTestUtil.AliceResponseDto();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userResponseDto.getUsername());

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User();
        when(userRepository.findByUsername(userResponseDto.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(anyLong(), any()))
                .thenReturn(Optional.ofNullable(task));
        when(commentMapper.toEntity(commentRequestDto)).thenReturn(commentWithoutId);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(expected);

        // when
        CommentResponseDto actual = commentService.createComment(commentRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("""
                Should return all available comments for the specific task
                """)
    void getAllForTask_twoComments_ReturnsAllComments() {
        CommentResponseDto addRefreshTokenCommentDto = CommentTestUtil.AddRefreshTokenCommentDto();
        Comment addRefreshTokenComment = CommentTestUtil.AddRefreshTokenComment();

        CommentResponseDto addGoogleCloudAPICommentDto = CommentTestUtil.AddGoogleCloudAPICommentDto();
        Comment addGoogleCloudAPIComment = CommentTestUtil.AddGoogleCloudAPIComment();

        List<Comment> comments
                = List.of(addRefreshTokenComment, addGoogleCloudAPIComment);
        Page<Comment> page = new PageImpl<>(comments);
        Pageable pageable = PageRequest.of(0, 10);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("alice.black");
        User mockUser = mock(User.class);
        when(userRepository.findByUsername("alice.black")).thenReturn(Optional.of(mockUser));
        Task mockTask = mock(Task.class);
        when(taskRepository.findTaskByIdAndAssignee(eq(2L), any()))
                .thenReturn(Optional.of(mockTask));

        when(commentRepository.findAllByTask_Id(2L, pageable)).thenReturn(page);
        when(commentMapper.toDto(addRefreshTokenComment))
                .thenReturn(addRefreshTokenCommentDto);
        when(commentMapper.toDto(addGoogleCloudAPIComment))
                .thenReturn(addGoogleCloudAPICommentDto);

        Page<CommentResponseDto> actual = commentService.getAllForTask(2L, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(commentRepository, times(1))
                .findAllByTask_Id(eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return empty page
                """)
    void getAllForTask_noComments_ReturnsEmptyPage() {
        List<Comment> comments = List.of();
        Page<Comment> page = new PageImpl<>(comments);
        Pageable pageable = PageRequest.of(0, 10);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("alice.black");
        User mockUser = new User();
        Task mockTask = new Task();
        when(userRepository.findByUsername("alice.black")).thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(eq(1L), any()))
                .thenReturn(Optional.of(mockTask));

        when(commentRepository.findAllByTask_Id(1L, pageable)).thenReturn(page);

        Page<CommentResponseDto> actual = commentService.getAllForTask(1L, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());
        verify(commentRepository, times(1))
                .findAllByTask_Id(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should delete existing Comment by its id
                """)
    void deleteCommentById_existingComment_Success() {
        Comment addRefreshTokenComment = CommentTestUtil.AddRefreshTokenComment();

        when(commentRepository.findById(1L))
                .thenReturn(Optional.ofNullable(addRefreshTokenComment));

        commentService.deleteCommentById(1L);

        verify(commentRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void deleteCommentById_nonExistingComment_NotFound() {
        Long nonExistingCommentId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteCommentById(nonExistingCommentId));

        verify(commentRepository, times(0))
                .deleteById(anyLong());
    }
}
