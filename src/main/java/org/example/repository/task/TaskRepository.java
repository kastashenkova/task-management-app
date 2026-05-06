package org.example.repository.task;

import java.util.Optional;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByProject_Id(Long projectId, Pageable pageable);

    Optional<Task> findTaskByIdAndAssignee(Long id, User assignee);

    Page<Task> findAll(Specification<Task> taskSpecification, Pageable pageable);
}
