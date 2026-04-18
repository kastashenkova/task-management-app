package org.example.mapper;

import org.example.config.MapperConfig;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.user.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User book);

    User toEntity(UserRegistrationRequestDto requestDto);
}
