package org.example.repository.task;

import java.util.List;

import org.example.model.project.Project;
import org.example.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByAssigneeId(Long assigneeId);

    void deleteTaskById(Long id);

    List<Task> findAllByProject(Project project);
}
