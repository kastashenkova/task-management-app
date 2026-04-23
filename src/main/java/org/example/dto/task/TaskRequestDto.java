package org.example.dto.task;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.example.model.task.Priority;
import org.example.model.task.Status;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class TaskRequestDto {
    @NotBlank
    @Length(min = 1, max = 100)
    private String name;
    @NotBlank
    @Length(min = 1, max = 500)
    private String description;
    @NotBlank
    @Length(min = 1, max = 20)
    private Priority priority;
    @NotBlank
    @Length(min = 1, max = 20)
    private Status status;
    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
    @NotEmpty
    private Long projectId;
    @NotEmpty
    private Long assigneeId;
}
