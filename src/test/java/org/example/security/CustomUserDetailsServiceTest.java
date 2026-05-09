package org.example.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.model.user.User;
import org.example.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("""
                Should return UserDetails for existing user
                """)
    void loadUserByUsername_existingUser_ReturnsUserDetails() {
        User user = new User()
                .setUsername("emily.roberts");

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UserDetails actual = service.loadUserByUsername(user.getUsername());

        assertNotNull(actual);
        verify(userRepository, times(1))
                .findByUsername(user.getUsername());
    }

    @Test
    @DisplayName("""
                Should return Not Found
                """)
    void loadUserByUsername_nonExistingUser_ReturnsNotFound() {
        String nonExistingUsername = "nonExistingUsername";

        when(userRepository.findByUsername(nonExistingUsername))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(nonExistingUsername));

        verify(userRepository, times(1))
                .findByUsername(nonExistingUsername);
    }
}
