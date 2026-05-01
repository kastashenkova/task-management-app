package org.example.dto.task;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.example.model.task.Priority;
import org.example.model.task.Status;

@Getter
@Setter
public class TaskResponseDto {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
    private Long labelId;
}
