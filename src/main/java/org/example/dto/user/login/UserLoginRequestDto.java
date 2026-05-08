package org.example.dto.user.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserLoginRequestDto {
    @NotBlank
    @Size(min = 3, max = 35, message = "{validation.username.size}")
    private String username;
    @NotBlank
    @Size(min = 8, max = 35, message = "{validation.password.size}")
    private String password;
}
