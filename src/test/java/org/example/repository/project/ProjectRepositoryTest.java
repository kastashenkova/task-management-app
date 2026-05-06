package org.example.repository.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.example.model.project.Project;
import org.example.repository.project.specification.EndDateSpecificationProvider;
import org.example.repository.project.specification.ProjectSearchParameters;
import org.example.repository.project.specification.ProjectSpecificationBuilder;
import org.example.repository.project.specification.ProjectSpecificationProviderManager;
import org.example.repository.project.specification.StatusSpecificationProvider;
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
        ProjectSpecificationProviderManager.class,
        EndDateSpecificationProvider.class,
        StatusSpecificationProvider.class
})
@ActiveProfiles("test")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectSpecificationProviderManager projectSpecificationProviderManager;

    @Test
    @DisplayName("""
            Should return all projects of the specific end date with pagination
            """)
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_projectsWithTheSameEndDate_ReturnsAllProjectsOfTheSameEndDate() {
        String[] requiredEndDate = {"2026-12-31"};
        ProjectSearchParameters searchParameter
                = new ProjectSearchParameters(null, requiredEndDate);
        ProjectSpecificationBuilder projectSpecificationBuilder
                = new ProjectSpecificationBuilder(projectSpecificationProviderManager);
        Specification<Project> specification = projectSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> actual = projectRepository.findAll(specification, pageable);
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all projects of the specific status with pagination
            """)
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_projectsWithTheSameStatus_ReturnsAllProjectsOfTheSameStatus() {
        String[] requiredStatus = {"IN_PROGRESS"};
        ProjectSearchParameters searchParameter
                = new ProjectSearchParameters(requiredStatus, null);
        ProjectSpecificationBuilder projectSpecificationBuilder
                = new ProjectSpecificationBuilder(projectSpecificationProviderManager);
        Specification<Project> specification = projectSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> actual = projectRepository.findAll(specification, pageable);
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all projects with pagination
            """)
    @Sql(scripts = "classpath:database/projects/add-projects-to-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/projects/delete-projects-from-projects-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_projectsWithoutSpecification_ReturnsAllProjects() {
        ProjectSearchParameters searchParameter
                = new ProjectSearchParameters(null, null);
        ProjectSpecificationBuilder projectSpecificationBuilder
                = new ProjectSpecificationBuilder(projectSpecificationProviderManager);
        Specification<Project> specification = projectSpecificationBuilder
                .buildSpecification(searchParameter);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> actual = projectRepository.findAll(specification, pageable);
        assertEquals(5, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return all projects of the specific assignee with pagination
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
    void findAllByAssigneeId_projectsWithTheSameAssignee_ReturnsAllProjectsWithTasksOfTheSameAssignee() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> actual = projectRepository.findAllByAssigneeId(3L, pageable);
        assertEquals(3, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Should return specific project of the specific user
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
    void findByAssigneeIdAndId_projectWithSpecificAssignee_ReturnsSpecificProjectWithTasksOfTheSameAssignee() {
        Optional<Project> actual = projectRepository.findByAssigneeIdAndId(5L, 1L);
        Optional<Project> expected = projectRepository.findById(1L);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should return no project of the specific user
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
    void findByAssigneeIdAndId_projectWithSpecificAssignee_ReturnsNull() {
        Optional<Project> actual = projectRepository.findByAssigneeIdAndId(2L, 1L);
        assertThat(actual).isEmpty();
    }
}
