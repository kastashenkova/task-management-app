package org.example.util;

import java.time.LocalDate;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.model.project.Project;
import org.example.model.project.Status;

public class ProjectTestUtil {
    public static final String PAYPAL_NAME = "Payment";
    public static final String PAYPAL_DESCRIPTION = "Develop your own PayPal system";
    public static final LocalDate PAYPAL_START_DATE = LocalDate.of(2026, 5, 6);
    public static final LocalDate PAYPAL_END_DATE = LocalDate.of(2026, 7, 8);

    public static final String MOBILE_BANKING_NAME = "Mobile Banking App";
    public static final String MOBILE_BANKING_DESCRIPTION = "Mobile application for managing bank accounts, transactions and money transfers";
    public static final LocalDate MOBILE_BANKING_START_DATE = LocalDate.of(2025, 11, 1);
    public static final LocalDate MOBILE_BANKING_END_DATE = LocalDate.of(2026, 4, 15);

    public static final Status INITIATED_STATUS = Status.INITIATED;
    public static final Status IN_PROGRESS_STATUS = Status.IN_PROGRESS;
    public static final Status COMPLETED_STATUS = Status.COMPLETED;


    public static ProjectRequestDto getProjectRequestDto(String name, String description,
                                     LocalDate startDate, LocalDate endDate,
                                     Status status) {
        return new ProjectRequestDto()
                .setName(name)
                .setDescription(description)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(status);
    }

    public static Project getProject(Long id, String name, String description,
                                     LocalDate startDate, LocalDate endDate,
                                     Status status) {
        return new Project()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(status);
    }

    public static ProjectResponseDto getProjectResponseDto(Long id, String name, String description,
                                     LocalDate startDate, LocalDate endDate,
                                     Status status) {
        return new ProjectResponseDto()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStatus(status);
    }

    public static ProjectRequestDto PayPalProjectRequestDto() {
        return getProjectRequestDto(PAYPAL_NAME, PAYPAL_DESCRIPTION,
                PAYPAL_START_DATE, PAYPAL_END_DATE, INITIATED_STATUS);
    }

    public static ProjectRequestDto MobileBankingAppProjectRequestDto() {
        return getProjectRequestDto(MOBILE_BANKING_NAME, MOBILE_BANKING_DESCRIPTION,
                MOBILE_BANKING_START_DATE, MOBILE_BANKING_END_DATE, IN_PROGRESS_STATUS);
    }

    public static Project PayPalProject() {
        return getProject(1L, PAYPAL_NAME, PAYPAL_DESCRIPTION,
                PAYPAL_START_DATE, PAYPAL_END_DATE, INITIATED_STATUS);
    }

    public static Project MobileBankingAppProject() {
        return getProject(2L, MOBILE_BANKING_NAME, MOBILE_BANKING_DESCRIPTION,
                MOBILE_BANKING_START_DATE, MOBILE_BANKING_END_DATE, COMPLETED_STATUS);
    }


    public static ProjectResponseDto PayPalProjectResponseDto() {
        return getProjectResponseDto(1L, PAYPAL_NAME, PAYPAL_DESCRIPTION,
                PAYPAL_START_DATE, PAYPAL_END_DATE, INITIATED_STATUS);
    }

    public static ProjectResponseDto MobileBankingAppProjectResponseDto() {
        return getProjectResponseDto(2L, MOBILE_BANKING_NAME, MOBILE_BANKING_DESCRIPTION,
                MOBILE_BANKING_START_DATE, MOBILE_BANKING_END_DATE, COMPLETED_STATUS);
    }
}
