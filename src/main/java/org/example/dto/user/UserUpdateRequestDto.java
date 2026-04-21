package org.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.user.registration.annotation.FieldMatch;
import org.example.model.user.Role;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@FieldMatch(first = "password",
        second = "repeatPassword",
        message = "Password and repeated password do not match")
public class UserUpdateRequestDto {
    @Length(min = 3, max = 35)
    private String username;
    @Email
    private String email;
    @Length(min = 8, max = 35)
    private String password;
    @Length(min = 8, max = 35)
    private String repeatPassword;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
