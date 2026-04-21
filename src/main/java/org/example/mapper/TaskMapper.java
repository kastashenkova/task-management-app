package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.model.task.Task;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    TaskResponseDto toDto(Task task);

    Task toEntity(TaskRequestDto requestDto);
}
