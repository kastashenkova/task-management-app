package org.example.dto.project;

import lombok.Getter;
import lombok.Setter;
import org.example.model.project.Status;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
}
