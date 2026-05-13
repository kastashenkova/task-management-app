package org.example.util;

import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.user.Role;
import org.example.model.user.User;

public class UserTestUtil {
    public static final String SARAH_USERNAME = "sarah.mitchell";
    public static final String SARAH_MITCHELL_EMAIL = "sarah.mitchell@company.com";
    public static final String SARAH_WHITE_EMAIL = "sarah.white@company.com";
    public static final String SARAH_PHONE = "+380977777777";
    public static final String SARAH_FIRST_NAME = "Sarah";
    public static final String SARAH_WHITE_LAST_NAME = "White";
    public static final String SARAH_MITCHELL_LAST_NAME = "Mitchell";

    public static final String ALICE_USERNAME = "alice.black";
    public static final String ALICE_EMAIL = "alice.black@company.com";
    public static final String ALICE_PHONE = "+380912345678";
    public static final String ALICE_FIRST_NAME = "Alice";
    public static final String ALICE_LAST_NAME = "Black";

    public static final String JOHN_USERNAME = "john.carter";
    public static final String JOHN_EMAIL = "john.carter@company.com";
    public static final String JOHN_PHONE = "+380988888888";
    public static final String JOHN_FIRST_NAME = "John";
    public static final String JOHN_LAST_NAME = "Carter";

    public static final String USER_ROLE_NAME = Role.RoleName.USER.name();
    public static final String ADMIN_ROLE_NAME = Role.RoleName.ADMIN.name();
    public static final String DEFAULT_PASSWORD = "password";

    public static Role UserRole() {
        return new Role()
                .setId(1L)
                .setName(Role.RoleName.USER);
    }

    public static Role AdminRole() {
        return new Role()
                .setId(2L)
                .setName(Role.RoleName.ADMIN);
    }

    public static UserRegistrationRequestDto getRegistrationRequestDto(
            String username, String email, String phoneNumber,
            String firstName, String lastName, String password,
            String repeatPassword) {
        return new UserRegistrationRequestDto()
                .setUsername(username)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setRepeatPassword(repeatPassword);
    }

    public static UserResponseDto getUserResponseDto(
            String username, String email, String phoneNumber,
            String firstName, String lastName, String role) {
        return new UserResponseDto()
                .setUsername(username)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRole(role);
    }

    public static User getUser(Long id, String username, String email, String phoneNumber,
                                  String firstName, String lastName, Role role) {
        return new User()
                .setId(id)
                .setUsername(username)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword("password")
                .setRole(role);
    }

    public static UserRegistrationRequestDto AliceRegistrationRequestDto() {
        return getRegistrationRequestDto(ALICE_USERNAME, ALICE_EMAIL,
                ALICE_PHONE, ALICE_FIRST_NAME, ALICE_LAST_NAME,
                DEFAULT_PASSWORD, DEFAULT_PASSWORD);
    }

    public static User Alice() {
        return getUser(2L, ALICE_USERNAME, ALICE_EMAIL,
                ALICE_PHONE, ALICE_FIRST_NAME, ALICE_LAST_NAME, UserRole());
    }

    public static User John() {
        return getUser(1L, JOHN_USERNAME, JOHN_EMAIL,
                JOHN_PHONE, JOHN_FIRST_NAME, JOHN_LAST_NAME, AdminRole());
    }

    public static UserResponseDto SarahMitchellResponseDto() {
        return getUserResponseDto(
                SARAH_USERNAME, SARAH_MITCHELL_EMAIL, SARAH_PHONE,
                SARAH_FIRST_NAME, SARAH_MITCHELL_LAST_NAME, USER_ROLE_NAME);
    }
    public static UserResponseDto SarahWhiteResponseDto() {
        return getUserResponseDto(
                SARAH_USERNAME, SARAH_WHITE_EMAIL, SARAH_PHONE,
                SARAH_FIRST_NAME, SARAH_WHITE_LAST_NAME, USER_ROLE_NAME);
    }

    public static UserResponseDto AliceResponseDto() {
        return getUserResponseDto(
                ALICE_USERNAME, ALICE_EMAIL, ALICE_PHONE,
                ALICE_FIRST_NAME, ALICE_LAST_NAME, USER_ROLE_NAME);
    }

    public static UserResponseDto JohnResponseDto() {
        return getUserResponseDto(
                JOHN_USERNAME, JOHN_EMAIL, JOHN_PHONE,
                JOHN_FIRST_NAME, JOHN_LAST_NAME, ADMIN_ROLE_NAME);
    }
}
