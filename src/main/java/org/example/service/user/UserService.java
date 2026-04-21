package org.example.service.user;

import java.util.Set;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.exception.RegistrationException;
import org.example.model.user.Role;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    UserResponseDto updateUserRolesById(Long userId, Set<Role> roles);

    UserResponseDto getMyInfo();

    UserResponseDto updateMyInfo(UserUpdateRequestDto userRequestDto);
}
