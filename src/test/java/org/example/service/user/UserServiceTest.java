package org.example.service.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.example.dto.user.RoleDto;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.mapper.user.UserMapper;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.repository.user.RoleRepository;
import org.example.repository.user.UserRepository;
import org.example.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("""
                Should register a new User
                """)
    void register_newUser_ReturnsNewUser() {
        // given
        UserRegistrationRequestDto requestDto = TestUtil.AliceRegistrationRequestDto();

        User userWithoutId = new User()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(requestDto.getPassword());

        User saved = TestUtil.Alice();

        UserResponseDto expected = TestUtil.AliceDto();

        when(userMapper.toEntity(requestDto)).thenReturn(userWithoutId);
        when(roleRepository.findRoleByName(Role.RoleName.USER))
                .thenReturn(Optional.of(saved.getRole()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(userWithoutId)).thenReturn(saved);
        when(userMapper.toDto(any(User.class))).thenReturn(expected);

        // when
        UserResponseDto actual = userService.register(requestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(userRepository, times(1)).save(userWithoutId);
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("""
                Should return existing User with updated role
                """)
    void updateUserRoleById_existingUser_ReturnsUpdatedUser() {
        // given
        RoleDto roleDto = new RoleDto()
                .setName("ADMIN");

        UserRegistrationRequestDto requestDto = TestUtil.AliceRegistrationRequestDto();

        User userWithoutId = new User()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(requestDto.getPassword());

        Role role = new Role()
                .setName(Role.RoleName.ADMIN);

        User updated = TestUtil.Alice()
                .setRole(role);

        UserResponseDto expected = TestUtil.AliceDto()
                .setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutId));
        when(roleRepository.findRoleByName(Role.RoleName.ADMIN))
                .thenReturn(Optional.of(role));
        when(userRepository.save(userWithoutId)).thenReturn(updated);
        when(userMapper.toDto(any(User.class))).thenReturn(expected);

        // when
        UserResponseDto actual = userService.updateUserRoleById(1L, roleDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(userRepository, times(1)).save(userWithoutId);
    }

    @Test
    @DisplayName("""
                Should return Not Found
                """)
    void updateUserRoleById_nonExistingUser_ReturnsNotFound() {
        RoleDto roleDto = new RoleDto()
                .setName("ADMIN");

        UserRegistrationRequestDto requestDto = TestUtil.AliceRegistrationRequestDto();

        User userWithoutId = new User()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(requestDto.getPassword());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRoleById(1L, roleDto));

        verify(userRepository, times(0)).save(userWithoutId);
    }

    @Test
    @DisplayName("""
            Should return user info
            """)
    void getMyInfo_loggedInUser_ReturnsLoggedInUserInfo() {
        UserResponseDto johnDto = TestUtil.JohnDto();
        User john = TestUtil.John();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(johnDto.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(johnDto.getUsername()))
                .thenReturn(Optional.of(john));
        when(userMapper.toDto(john)).thenReturn(johnDto);

        UserResponseDto actual = userService.getMyInfo();

        assertNotNull(actual);
        verify(userRepository, times(1))
                .findByUsername(anyString());
    }

    @Test
    @DisplayName("""
                Should return existing User with updated role
                """)
    void updateMyInfo_loggedInUser_ReturnsUpdatedUser() {
        // given
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setUsername("new.user");
        userUpdateRequestDto.setEmail("new.user@company.com");
        userUpdateRequestDto.setFirstName("New");
        userUpdateRequestDto.setLastName("User");
        userUpdateRequestDto.setPassword("password");
        userUpdateRequestDto.setRepeatPassword("password");

        User userWithoutId = new User();
        userWithoutId.setUsername(userUpdateRequestDto.getUsername());
        userWithoutId.setEmail(userUpdateRequestDto.getEmail());
        userWithoutId.setPhoneNumber(userUpdateRequestDto.getPhoneNumber());
        userWithoutId.setFirstName(userUpdateRequestDto.getFirstName());
        userWithoutId.setLastName(userUpdateRequestDto.getLastName());
        userWithoutId.setPassword(userUpdateRequestDto.getPassword());

        User updated = new User();
        updated.setId(1L);
        updated.setUsername(userWithoutId.getUsername());
        updated.setEmail(userWithoutId.getEmail());
        updated.setPhoneNumber(userWithoutId.getPhoneNumber());
        updated.setFirstName(userWithoutId.getFirstName());
        updated.setLastName(userWithoutId.getLastName());
        updated.setPassword(userWithoutId.getPassword());
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.USER);
        updated.setRole(role);

        UserResponseDto expected = TestUtil.AliceDto();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(expected.getUsername());

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(expected.getUsername()))
                .thenReturn(Optional.of(userWithoutId));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(userWithoutId)).thenReturn(updated);
        when(userMapper.toDto(any(User.class))).thenReturn(expected);

        // when
        UserResponseDto actual = userService.updateMyInfo(userUpdateRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(userRepository, times(1)).save(userWithoutId);
        verify(passwordEncoder, times(1)).encode(anyString());
    }
}
