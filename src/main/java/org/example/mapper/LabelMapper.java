package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.model.label.Label;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    LabelResponseDto toDto(Label label);

    Label toEntity(LabelRequestDto requestDto);
}
