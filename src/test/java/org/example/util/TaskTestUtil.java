package org.example.util;

import static org.example.util.LabelTestUtil.MergeConflictLabel;
import static org.example.util.LabelTestUtil.PullRequestLabel;
import static org.example.util.ProjectTestUtil.MobileBankingAppProject;
import static org.example.util.ProjectTestUtil.PayPalProject;
import static org.example.util.UserTestUtil.John;

import java.time.LocalDate;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.model.label.Label;
import org.example.model.project.Project;
import org.example.model.task.Priority;
import org.example.model.task.Status;
import org.example.model.task.Task;
import org.example.model.user.User;

public class TaskTestUtil {
    public static final String BUILD_PAYROLL_NAME = "Build Payroll Module";
    public static final String BUILD_PAYROLL_DESCRIPTION = "Develop salary calculation module with tax deductions and automated monthly payslip generation";
    public static final LocalDate BUILD_PAYROLL_DUE_DATE = LocalDate.of(2026, 7, 15);

    public static final String ADD_COUNTRY_NAME = "Add Payment Country";
    public static final String ADD_COUNTRY_DESCRIPTION = "Add country #170 to the payment system";
    public static final LocalDate ADD_COUNTRY_DUE_DATE = LocalDate.of(2026, 10, 26);

    public static final Priority MEDIUM_PRIORITY = Priority.MEDIUM;
    public static final Priority HIGH_PRIORITY = Priority.HIGH;

    public static final Status IN_PROGRESS_STATUS = Status.IN_PROGRESS;
    public static final Status NOT_STARTED_STATUS = Status.NOT_STARTED;

    public static TaskRequestDto getTaskRequestDto(String name,
                                                   String description,
                                                   Priority priority,
                                                   org.example.model.task.Status status,
                                                   LocalDate dueDate,
                                                   Long projectId,
                                                   Long assigneeId,
                                                   Long labelId) {
        return new TaskRequestDto()
                .setName(name)
                .setDescription(description)
                .setPriority(priority)
                .setStatus(status)
                .setDueDate(dueDate)
                .setProjectId(projectId)
                .setAssigneeId(assigneeId)
                .setLabelId(labelId);
    }

    public static Task getTask(Long id,
                               String name,
                               String description,
                               Priority priority,
                               org.example.model.task.Status status,
                               LocalDate dueDate,
                               Project project,
                               User assignee,
                               Label label) {
        return new Task()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setPriority(priority)
                .setStatus(status)
                .setDueDate(dueDate)
                .setProject(project)
                .setAssignee(assignee)
                .setLabel(label);
    }

    public static TaskResponseDto getTaskResponseDto(Long id,
                                                     String name,
                                                     String description,
                                                     Priority priority,
                                                     org.example.model.task.Status status,
                                                     LocalDate dueDate,
                                                     Long projectId,
                                                     Long assigneeId,
                                                     Long labelId) {
        return new TaskResponseDto()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setPriority(priority)
                .setStatus(status)
                .setDueDate(dueDate)
                .setProjectId(projectId)
                .setAssigneeId(assigneeId)
                .setLabelId(labelId);
    }

    public static TaskRequestDto BuildPayrollModuleTaskRequestDto() {
        return getTaskRequestDto(BUILD_PAYROLL_NAME, BUILD_PAYROLL_DESCRIPTION,
                MEDIUM_PRIORITY, NOT_STARTED_STATUS, BUILD_PAYROLL_DUE_DATE,
                PayPalProject().getId(), John().getId(), PullRequestLabel().getId());
    }

    public static Task BuildPayrollModuleTask() {
        return getTask(3L, BUILD_PAYROLL_NAME, BUILD_PAYROLL_DESCRIPTION,
                MEDIUM_PRIORITY, NOT_STARTED_STATUS, BUILD_PAYROLL_DUE_DATE,
                PayPalProject(), John(), PullRequestLabel());
    }

    public static TaskResponseDto BuildPayrollModuleTaskResponseDto() {
        return getTaskResponseDto(3L, BUILD_PAYROLL_NAME, BUILD_PAYROLL_DESCRIPTION,
                MEDIUM_PRIORITY, NOT_STARTED_STATUS, BUILD_PAYROLL_DUE_DATE,
                PayPalProject().getId(), John().getId(), PullRequestLabel().getId());
    }

    public static Task AddPaymentCountryTask() {
        return getTask(4L, ADD_COUNTRY_NAME, ADD_COUNTRY_DESCRIPTION,
                HIGH_PRIORITY, IN_PROGRESS_STATUS, ADD_COUNTRY_DUE_DATE,
                MobileBankingAppProject(), John(), MergeConflictLabel());
    }

    public static TaskResponseDto AddPaymentCountryTaskResponseDto() {
        return getTaskResponseDto(4L, ADD_COUNTRY_NAME, ADD_COUNTRY_DESCRIPTION,
                HIGH_PRIORITY, IN_PROGRESS_STATUS, ADD_COUNTRY_DUE_DATE,
                MobileBankingAppProject().getId(), John().getId(), MergeConflictLabel().getId());
    }
}
