package org.example.mapper.user;

import org.example.config.MapperConfig;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "role",
            expression = "java(user.getRole() != null ? user.getRole().getName().name() : null)")
    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto requestDto);
}
