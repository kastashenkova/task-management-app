package org.example.repository.attachment;

import org.example.model.Attachment;
import org.example.model.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Page<Attachment> findAllByTask(Task task, Pageable pageable);
}
