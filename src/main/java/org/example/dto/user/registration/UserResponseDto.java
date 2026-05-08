package org.example.dto.user.registration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String role;
}
