package org.example.service.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.mapper.AttachmentMapper;
import org.example.model.Attachment;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.attachment.AttachmentRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.example.service.third_party.DropboxService;
import org.example.util.AttachmentTestUtil;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private DropboxService dropboxService;

    @Test
    @DisplayName("""
                Should create a new Attachment
                """)
    void createAttachment_newAttachment_ReturnsNewAttachment() throws Exception {
        // given
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUserName");
        Task mockTask = new Task()
                .setId(1L)
                .setAssignee(mockUser);

        String dropboxFileId = "dbx_prefix_actualFilename.pdf";

        Attachment saved = new Attachment()
                .setId(1L)
                .setTask(mockTask)
                .setDropboxFileId(dropboxFileId)
                .setFilename("actualFilename.pdf")
                .setUploadDate(LocalDateTime.now());

        AttachmentResponseDto expected = AttachmentTestUtil.AttachmentForBuildPayrollModuleTaskDto();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(mockTask.getId(), mockUser))
                .thenReturn(Optional.of(mockTask));

        MultipartFile mockFile = mock(MultipartFile.class);
        when(dropboxService.uploadFile(mockFile))
                .thenReturn(dropboxFileId);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(saved);
        when(attachmentMapper.toDto(any(Attachment.class))).thenReturn(expected);

        // when
        AttachmentResponseDto actual = attachmentService.createAttachment(
                mockTask.getId(), mockFile);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(attachmentRepository, times(1))
                .save(any(Attachment.class));
    }

    @Test
    @DisplayName("""
                Should return all available attachments
                """)
    void getAllForTask_twoAttachmentsInTask_ReturnsAllAttachments() {
        AttachmentResponseDto buildPayrollModuleAttachmentDto = AttachmentTestUtil.AttachmentForBuildPayrollModuleTaskDto();
        Attachment buildPayrollModuleAttachment = AttachmentTestUtil.AttachmentForBuildPayrollModuleTask();

        AttachmentResponseDto addPaymentCountryAttachmentDto = AttachmentTestUtil.AttachmentForAddPaymentCountryTaskDto();
        Attachment addPaymentCountryAttachment = AttachmentTestUtil.AttachmentForAddPaymentCountryTask();

        List<Attachment> attachments
                = List.of(buildPayrollModuleAttachment, addPaymentCountryAttachment);
        Page<Attachment> page = new PageImpl<>(attachments);
        Pageable pageable = PageRequest.of(0, 10);

        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        Long taskId = buildPayrollModuleAttachmentDto.getTaskId();
        Task mockTask = new Task()
                .setId(taskId);
        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(taskId, mockUser))
               .thenReturn(Optional.of(mockTask));
        when(attachmentMapper.toDto(buildPayrollModuleAttachment))
                .thenReturn(buildPayrollModuleAttachmentDto);
        when(attachmentMapper.toDto(addPaymentCountryAttachment))
                .thenReturn(addPaymentCountryAttachmentDto);
        when(attachmentRepository.findAllByTask_Id(taskId, pageable)).thenReturn(page);

        Page<AttachmentResponseDto> actual = attachmentService.getAllForTask(
                taskId, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(attachmentRepository, times(1))
                .findAllByTask_Id(eq(mockTask.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return empty page
                """)
    void getAllForTask_noAttachments_ReturnsEmptyPage() {
        List<Attachment> attachments = List.of();
        Page<Attachment> page = new PageImpl<>(attachments);
        Pageable pageable = PageRequest.of(0, 10);

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        Long mockTaskId = 1L;
        Task mockTask = new Task()
                .setId(mockTaskId);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(taskRepository.findTaskByIdAndAssignee(mockTaskId, mockUser))
                .thenReturn(Optional.ofNullable(mockTask));
        when(attachmentRepository.findAllByTask_Id(mockTaskId, pageable))
                .thenReturn(page);

        Page<AttachmentResponseDto> actual = attachmentService
                .getAllForTask(mockTaskId, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());
        verify(attachmentRepository, times(1))
                .findAllByTask_Id(eq(mockTaskId), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Get existing Attachment by its id
                """)
    void retrieveAttachment_existingAttachment_ReturnsTheAttachment() {
        AttachmentResponseDto buildPayrollModuleAttachmentDto = AttachmentTestUtil
                .AttachmentForBuildPayrollModuleTaskDto();
        Attachment buildPayrollModuleAttachment = AttachmentTestUtil.AttachmentForBuildPayrollModuleTask();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUser");
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));

        Long taskId = buildPayrollModuleAttachmentDto.getTaskId();
        Task mockTask = mock(Task.class);
        when(taskRepository.findTaskByIdAndAssignee(taskId, mockUser))
                .thenReturn(Optional.of(mockTask));
        when(attachmentRepository.findById(1L))
                .thenReturn(Optional.of(buildPayrollModuleAttachment));
        byte[] mockFileBytes = "mock-file-content".getBytes();
        when(dropboxService.downloadFile(buildPayrollModuleAttachment.getDropboxFileId()))
                .thenReturn(mockFileBytes);

        ResponseEntity<byte[]> actual = attachmentService.retrieveAttachment(1L);

        assertNotNull(actual);
        verify(attachmentRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void retrieveAttachment_nonExistingAttachment_NotFound() {
        Long nonExistingAttachmentId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.retrieveAttachment(nonExistingAttachmentId));

        verify(attachmentRepository, times(1)).findById(nonExistingAttachmentId);
    }

    @Test
    @DisplayName("""
                Should delete existing Attachment by its id
                """)
    void deleteAttachment_existingAttachment_Success() {
        AttachmentResponseDto buildPayrollModuleAttachmentDto = AttachmentTestUtil
                .AttachmentForBuildPayrollModuleTaskDto();
        Attachment buildPayrollModuleAttachment = AttachmentTestUtil.AttachmentForBuildPayrollModuleTask();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        User mockUser = new User()
                .setId(1L)
                .setUsername("mockUser");
        when(authentication.getName()).thenReturn(mockUser.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        doNothing().when(dropboxService).deleteFile(
                buildPayrollModuleAttachmentDto.getDropboxFileId());
        when(attachmentRepository.findById(1L))
                .thenReturn(Optional.of(buildPayrollModuleAttachment));

        Long taskId = buildPayrollModuleAttachmentDto.getTaskId();
        Task mockTask = mock(Task.class);
        when(taskRepository.findTaskByIdAndAssignee(taskId, mockUser))
                .thenReturn(Optional.of(mockTask));

        attachmentService.deleteAttachment(1L);

        verify(attachmentRepository, times(1))
                .delete(buildPayrollModuleAttachment);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void deleteAttachment_nonExistingAttachment_NotFound() {
        Long nonExistingAttachmentId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.deleteAttachment(nonExistingAttachmentId));

        verify(attachmentRepository, times(0)).deleteById(anyLong());
    }
}
