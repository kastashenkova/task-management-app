package org.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.dto.user.login.UserLoginRequestDto;
import org.example.dto.user.login.UserLoginResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService service;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("""
                Should return access token
                """)
    void authenticate_validCredentials_ReturnsToken() {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto()
                .setUsername("john.carter");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn(userLoginRequestDto.getUsername());
        String mockToken = "mockToken";
        when(jwtUtil.generateToken(userLoginRequestDto.getUsername()))
                .thenReturn(mockToken);

        UserLoginResponseDto actual = service.authenticate(userLoginRequestDto);

        assertNotNull(actual);
        assertEquals(mockToken, actual.token());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1))
                .generateToken(userLoginRequestDto.getUsername());
    }

    @Test
    @DisplayName("""
                Should return Bad Credentials Exception
                """)
    void authenticate_invalidCredentials_shouldThrowException() {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto()
                .setUsername("john.carter");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class,
                () -> service.authenticate(userLoginRequestDto));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(0)).generateToken(anyString());
    }
}
