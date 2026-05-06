package org.example.repository.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.task.specification.PrioritySpecificationProvider;
import org.example.repository.task.specification.StatusSpecificationProvider;
import org.example.repository.task.specification.TaskSearchParameters;
import org.example.repository.task.specification.TaskSpecificationBuilder;
import org.example.repository.task.specification.TaskSpecificationProviderManager;
import org.example.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TaskSpecificationProviderManager.class,
        PrioritySpecificationProvider.class,
        StatusSpecificationProvider.class
})
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskSpecificationProviderManager taskSpecificationProviderManager;

    @Test
    @DisplayName("""
            Should return all tasks of the specific priority with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_tasksWithTheSamePriority_ReturnsAllTasksOfTheSamePriority() {
        String[] requiredPriority = {"MEDIUM"};
        TaskSearchParameters searchParameter
                = new TaskSearchParameters(requiredPriority, null);
        TaskSpecificationBuilder taskSpecificationBuilder
                = new TaskSpecificationBuilder(taskSpecificationProviderManager);
        Specification<Task> specification = taskSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> actual = taskRepository.findAll(specification, pageable);
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all tasks of the specific priority with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_tasksWithTheSameStatus_ReturnsAllTasksOfTheSameStatus() {
        String[] requiredStatus = {"COMPLETED"};
        TaskSearchParameters searchParameter
                = new TaskSearchParameters(null, requiredStatus);
        TaskSpecificationBuilder projectSpecificationBuilder
                = new TaskSpecificationBuilder(taskSpecificationProviderManager);
        Specification<Task> specification = projectSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> actual = taskRepository.findAll(specification, pageable);
        assertEquals(1, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all tasks with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_tasksWithoutSpecification_ReturnsAllTasks() {
        TaskSearchParameters searchParameter
                = new TaskSearchParameters(null, null);
        TaskSpecificationBuilder projectSpecificationBuilder
                = new TaskSpecificationBuilder(taskSpecificationProviderManager);
        Specification<Task> specification = projectSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> actual = taskRepository.findAll(specification, pageable);
        assertEquals(5, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all tasks of the specific project with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByProject_Id_tasksWithinTheSameProject_ReturnsAllTasksOfTheSameProject() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> actual = taskRepository.findAllByProject_Id(1L, pageable);
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return no tasks of the specific project with pagination
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByProject_Id_noTasksWithinTheProject_ReturnsNoTasksOfTheSameProject() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> actual = taskRepository.findAllByProject_Id(2L, pageable);
        assertEquals(0, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return specific task of the specific user
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findTaskByIdAndAssignee_specificTaskWithSpecificAssignee_ReturnsSpecificTaskOfTheAssignee() {
        User assignee = userRepository.findById(3L).orElse(null);
        Optional<Task> actual = taskRepository.findTaskByIdAndAssignee(1L, assignee);
        Optional<Task> expected = taskRepository.findById(1L);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should return no task of the specific user
            """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/tasks/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/tasks/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findTaskByIdAndAssignee_noTaskWithSpecificAssignee_ReturnsNoSpecificTaskOfTheAssignee() {
        User assignee = userRepository.findById(5L).orElse(null);
        Optional<Task> actual = taskRepository.findTaskByIdAndAssignee(1L, assignee);
        assertThat(actual).isEmpty();
    }
}
