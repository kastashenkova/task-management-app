package org.example.util;

import java.time.LocalDate;
import org.example.dto.project.ProjectResponseDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.project.Status;

public class TestUtil {

    public static UserResponseDto SarahWhiteDto() {
        UserResponseDto user = new UserResponseDto();
        user.setUsername("sarah.mitchell");
        user.setEmail("sarah.white@company.com");
        user.setPhoneNumber("+380977777777");
        user.setFirstName("Sarah");
        user.setLastName("White");
        user.setRole("USER");
        return user;
    }

    public static UserResponseDto SarahMitchellDto() {
        UserResponseDto user = new UserResponseDto();
        user.setUsername("sarah.mitchell");
        user.setEmail("sarah.mitchell@company.com");
        user.setPhoneNumber("+380977777777");
        user.setFirstName("Sarah");
        user.setLastName("Mitchell");
        user.setRole("USER");
        return user;
    }

    public static UserResponseDto AliceDto() {
        UserResponseDto user = new UserResponseDto();
        user.setUsername("alice.black");
        user.setEmail("alice.black@company.com");
        user.setPhoneNumber("+380912345678");
        user.setFirstName("Alice");
        user.setLastName("Black");
        user.setRole("USER");
        return user;
    }

    public static UserResponseDto JohnDto() {
        UserResponseDto user = new UserResponseDto();
        user.setUsername("john.carter");
        user.setEmail("john.carter@company.com");
        user.setPhoneNumber("+380988888888");
        user.setFirstName("John");
        user.setLastName("Carter");
        user.setRole("ADMIN");
        return user;
    }

    public static ProjectResponseDto PayPalProjectDto() {
        ProjectResponseDto project = new ProjectResponseDto();
        project.setId(1L);
        project.setName("Payment");
        project.setDescription("Develop your own PayPal system");
        project.setStartDate(LocalDate.of(2026, 5, 6));
        project.setEndDate(LocalDate.of(2026, 7, 8));
        project.setStatus(Status.INITIATED);
        return project;
    }

    public static ProjectResponseDto MobileBankingAppProjectDto() {
        ProjectResponseDto project = new ProjectResponseDto();
        project.setId(2L);
        project.setName("Mobile Banking App");
        project.setDescription("Mobile application for managing bank accounts, transactions and money transfers");
        project.setStartDate(LocalDate.of(2025, 11, 1));
        project.setEndDate(LocalDate.of(2026, 4, 15));
        project.setStatus(Status.COMPLETED);
        return project;
    }
}
