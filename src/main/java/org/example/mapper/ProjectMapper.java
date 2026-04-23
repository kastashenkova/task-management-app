package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.model.project.Project;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    ProjectResponseDto toDto(Project project);

    Project toEntity(ProjectRequestDto requestDto);
}
