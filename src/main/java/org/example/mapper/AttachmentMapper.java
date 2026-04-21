package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.attachment.AttachmentRequestDto;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.model.Attachment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    AttachmentResponseDto toDto(Attachment attachment);

    Attachment toEntity(AttachmentRequestDto requestDto);
}
