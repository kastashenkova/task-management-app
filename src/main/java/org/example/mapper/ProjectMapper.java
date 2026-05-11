package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.model.project.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    ProjectResponseDto toDto(Project project);

    Project toEntity(ProjectRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProjectRequestDto requestDto,
                       @MappingTarget Project project);
}
