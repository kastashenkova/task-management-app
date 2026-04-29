package org.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.user.registration.annotation.FieldMatch;
import org.hibernate.validator.constraints.Length;

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
    @Length(min = 8, max = 13)
    @Pattern(
            regexp = "^(\\+380|0)\\d{9}$",
            message = "Phone number must be in format +380XXXXXXXXX or 0XXXXXXXXX"
    )
    private String phoneNumber;
    @Length(min = 8, max = 35)
    private String password;
    @Length(min = 8, max = 35)
    private String repeatPassword;
    private String firstName;
    private String lastName;
}
