package org.example.service.user;

import org.example.dto.user.RoleDto;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;

    UserResponseDto updateUserRoleById(Long userId, RoleDto roleDto);

    UserResponseDto getMyInfo();

    UserResponseDto updateMyInfo(UserUpdateRequestDto userRequestDto);
}
