package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.comment.CommentRequestDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.model.Comment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    CommentResponseDto toDto(Comment comment);

    Comment toEntity(CommentRequestDto requestDto);
}
