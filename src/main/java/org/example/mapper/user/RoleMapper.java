package org.example.mapper.user;

import org.example.config.MapperConfig;
import org.example.dto.user.RoleDto;
import org.example.model.user.Role;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    Role toEntity(RoleDto requestDto);

    RoleDto toDto(Role role);
}
