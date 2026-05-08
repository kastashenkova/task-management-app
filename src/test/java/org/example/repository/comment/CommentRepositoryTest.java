package org.example.repository.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.model.Comment;
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
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("""
            Should return all comments of the specific task with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByTask_Id_commentsWithinTheSameTask_ReturnsAllCommentsOfTheSameTask() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> actual = commentRepository.findAllByTask_Id(2L, pageable);
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return no comments of the specific task with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByTask_Id_noCommentsWithinTheTask_ReturnsNoCommentOfTheSameTask() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> actual = commentRepository.findAllByTask_Id(5L, pageable);
        assertEquals(0, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return true
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByIdAndUser_Username_specificCommentOfTheSpecificUser_ReturnsTrue() {
        boolean actual = commentRepository.existsByIdAndUser_Username(1L, "michael.scott");
        assertTrue(actual);
    }

    @Test
    @DisplayName("""
            Should return false
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/comments/add-comments-to-comments-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/comments/delete-comments-from-comments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByIdAndUser_Username_specificCommentOfTheSpecificUser_ReturnsFalse() {
        boolean actual = commentRepository.existsByIdAndUser_Username(5L, "michael.scott");
        assertFalse(actual);
    }
}
