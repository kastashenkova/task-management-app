package org.example.service.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.exception.RegistrationException;
import org.example.mapper.UserMapper;
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
        if (userRepository.findByUsername(request.getUsername().toLowerCase()).isEmpty()) {
            throw new RegistrationException(
                    "User with such username already exists: "
                            + request.getUsername());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role defaultRole = roleRepository.findRoleByName(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role USER not found: " + Role.RoleName.USER));
        user.setRoles(Set.of(defaultRole));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserRolesById(Long userId, Set<Role> roles) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException(
                    "User with such id not found: " + userId);
        }
        for (Role role : roles) {
            if (roleRepository.findRoleByName(role.getName()).isEmpty()) {
                throw new EntityNotFoundException("Role with such name not found: "
                        + role.getName());
            }
        }
        User user = userRepository.findById(userId).get();
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getMyInfo() {
        Long userId = Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName());
        return userRepository.getUserById(userId);
    }

    @Override
    @Transactional
    public UserResponseDto updateMyInfo(UserUpdateRequestDto userRequestDto) {
        Long userId = Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User with such id not found: " + userId);
        }
        User user = userRepository.findById(userId).get();
        if (userRequestDto.getUsername() != null && userRequestDto.getUsername().isEmpty()) {
            user.setUsername(userRequestDto.getUsername());
        }
        if (userRequestDto.getEmail() != null && userRequestDto.getEmail().isEmpty()) {
            user.setEmail(userRequestDto.getEmail());
        }
        if (userRequestDto.getFirstName() != null && userRequestDto.getFirstName().isEmpty()) {
            user.setFirstName(userRequestDto.getFirstName());
        }
        if (userRequestDto.getLastName() != null && userRequestDto.getLastName().isEmpty()) {
            user.setLastName(userRequestDto.getLastName());
        }
        if (userRequestDto.getPassword() != null && userRequestDto.getPassword().isEmpty()) {
            user.setPassword(userRequestDto.getPassword());
        }
        if (userRequestDto.getRoles() != null && userRequestDto.getRoles().isEmpty()) {
            return updateUserRolesById(userId, userRequestDto.getRoles());
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
