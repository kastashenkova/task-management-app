package org.example.service.attachment;

import org.example.mapper.AttachmentMapper;
import org.example.repository.attachment.AttachmentRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

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


}
