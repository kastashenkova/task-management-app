package org.example.dto.user.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    @NotBlank
    @Size(min = 3, max = 35, message = "{validation.username.size}")
    private String username;
    @NotBlank
    @Size(min = 8, max = 35, message = "{validation.password.size}")
    private String password;
}
