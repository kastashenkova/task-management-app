package org.example.repository.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.model.Attachment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AttachmentRepositoryTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    @DisplayName("""
            Should return all attachments of the specific task with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByTask_Id_attachmentsWithinTheSameTask_ReturnsAllAttachmentsOfTheSameTask() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attachment> actual = attachmentRepository.findAllByTask_Id(3L, pageable);
        assertEquals(3, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return no attachments of the specific task with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByTask_Id_noAttachmentsWithinTheTask_ReturnsNoAttachmentOfTheSameTask() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attachment> actual = attachmentRepository.findAllByTask_Id(4L, pageable);
        assertEquals(0, actual.getTotalElements());
    }
}
