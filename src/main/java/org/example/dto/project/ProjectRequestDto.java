package org.example.dto.project;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.example.model.project.Status;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class ProjectRequestDto {
    @NotBlank
    @Length(min = 1, max = 100, message = "{validation.name.size}")
    private String name;
    @NotBlank
    @Length(min = 1, max = 500, message = "{validation.description.size}")
    private String description;
    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    @NotBlank
    @Length(min = 1, max = 20, message = "{validation.status.size}")
    private Status status;
}
