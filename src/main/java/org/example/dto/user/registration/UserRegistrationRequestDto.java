package org.example.dto.user.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.regex.qual.Regex;
import org.example.dto.user.registration.annotation.FieldMatch;
import org.example.model.user.Role;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@FieldMatch(first = "password",
        second = "repeatPassword",
        message = "Password and repeated password do not match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Length(min = 3, max = 35)
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(min = 8, max = 13)
    @Pattern(
            regexp = "^(\\+380|0)\\d{9}$",
            message = "Phone number must be in format +380XXXXXXXXX or 0XXXXXXXXX"
    )
    private String phoneNumber;
    @NotBlank
    @Length(min = 8, max = 35)
    private String password;
    @NotBlank
    @Length(min = 8, max = 35)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
