package org.example.repository.task;

import org.example.model.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    void deleteTaskById(Long id);

    Page<Task> findAllByProject_Id(Long projectId, Pageable pageable);
}
