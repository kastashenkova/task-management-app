package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.model.label.Label;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    LabelResponseDto toDto(Label label);

    Label toEntity(LabelRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(LabelRequestDto requestDto,
                       @MappingTarget Label label);
}
