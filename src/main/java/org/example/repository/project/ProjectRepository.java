package org.example.repository.project;

import org.example.model.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT DISTINCT p FROM Project p JOIN Task t ON t.project = p WHERE t.assignee.id = :userId")
    Page<Project> findAllByAssigneeId(@Param("userId") Long userId, Pageable pageable);
}
