package org.example.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.RoleDto;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.exception.RegistrationException;
import org.example.mapper.user.UserMapper;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.repository.user.RoleRepository;
import org.example.repository.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByUsername(request.getUsername().toLowerCase()).isPresent()) {
            throw new RegistrationException(
                    "User with such username already exists: "
                            + request.getUsername());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role defaultRole = roleRepository.findRoleByName(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role USER not found: " + Role.RoleName.USER));
        user.setRole(defaultRole);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserRoleById(Long userId,
                                              RoleDto roleDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with such id not found: " + userId));
        Role.RoleName roleName = Role.RoleName.valueOf(roleDto.getName().toUpperCase());
        Role role = roleRepository.findRoleByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role with such name not found: " + roleName));
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateMyInfo(UserUpdateRequestDto userRequestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        if (userRequestDto.getUsername() != null && !userRequestDto.getUsername().isEmpty()) {
            user.setUsername(userRequestDto.getUsername());
        }
        if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()) {
            user.setEmail(userRequestDto.getEmail());
        }
        if (userRequestDto.getPhoneNumber() != null && !userRequestDto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(userRequestDto.getPhoneNumber());
        }
        if (userRequestDto.getFirstName() != null && !userRequestDto.getFirstName().isEmpty()) {
            user.setFirstName(userRequestDto.getFirstName());
        }
        if (userRequestDto.getLastName() != null && !userRequestDto.getLastName().isEmpty()) {
            user.setLastName(userRequestDto.getLastName());
        }
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            user.setPassword(userRequestDto.getPassword());
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
