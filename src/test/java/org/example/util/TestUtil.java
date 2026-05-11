package org.example.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.dto.comment.CommentResponseDto;
import org.example.dto.label.LabelResponseDto;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.dto.task.TaskRequestDto;
import org.example.dto.task.TaskResponseDto;
import org.example.dto.user.registration.UserRegistrationRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.Attachment;
import org.example.model.Comment;
import org.example.model.label.Color;
import org.example.model.label.Label;
import org.example.model.project.Project;
import org.example.model.project.Status;
import org.example.model.task.Priority;
import org.example.model.task.Task;
import org.example.model.user.Role;
import org.example.model.user.User;

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

    public static UserRegistrationRequestDto AliceRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setUsername("alice.black")
                .setEmail("alice.black@company.com")
                .setPhoneNumber("+380912345678")
                .setFirstName("Alice")
                .setLastName("Black")
                .setPassword("password")
                .setRepeatPassword("password");
    }

    public static UserResponseDto AliceDto() {
        UserRegistrationRequestDto requestDto = TestUtil.AliceRegistrationRequestDto();
        return new UserResponseDto()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setRole("USER");
    }

    public static User Alice() {
        UserRegistrationRequestDto requestDto = TestUtil.AliceRegistrationRequestDto();
        Role role = new Role()
                .setId(1L)
                .setName(Role.RoleName.USER);
        return new User()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(requestDto.getPassword())
                .setRole(role);
    }

    public static UserRegistrationRequestDto JohnRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setUsername("john.carter")
                .setEmail("john.carter@company.com")
                .setPhoneNumber("+380988888888")
                .setFirstName("John")
                .setLastName("Carter")
                .setPassword("password")
                .setRepeatPassword("password");
    }

    public static UserResponseDto JohnDto() {
        UserRegistrationRequestDto requestDto = TestUtil.JohnRegistrationRequestDto();
        return new UserResponseDto()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setRole("ADMIN");
    }

    public static User John() {
        UserRegistrationRequestDto requestDto = TestUtil.JohnRegistrationRequestDto();
        Role role = new Role()
                .setId(2L)
                .setName(Role.RoleName.ADMIN);
        return new User()
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPhoneNumber(requestDto.getPhoneNumber())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setPassword(requestDto.getPassword())
                .setRole(role);
    }

    public static ProjectRequestDto PayPalProjectRequestDto() {
        return new ProjectRequestDto()
                .setName("Payment")
                .setDescription("Develop your own PayPal system")
                .setStartDate(LocalDate.of(2026, 5, 6))
                .setEndDate(LocalDate.of(2026, 7, 8))
                .setStatus(Status.INITIATED);
    }

    public static ProjectResponseDto PayPalProjectDto() {
        ProjectRequestDto requestDto = TestUtil.PayPalProjectRequestDto();
        return new ProjectResponseDto()
                .setId(1L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription())
                .setStartDate(requestDto.getStartDate())
                .setEndDate(requestDto.getEndDate())
                .setStatus(requestDto.getStatus());
    }

    public static Project PayPalProject() {
        ProjectResponseDto payPalProjectDto = TestUtil.PayPalProjectDto();
        return new Project()
                .setId(payPalProjectDto.getId())
                .setName(payPalProjectDto.getName())
                .setDescription(payPalProjectDto.getDescription())
                .setStartDate(payPalProjectDto.getStartDate())
                .setEndDate(payPalProjectDto.getEndDate())
                .setStatus(payPalProjectDto.getStatus());
    }

    public static ProjectRequestDto MobileBankingAppProjectRequestDto() {
        return new ProjectRequestDto()
                .setName("Mobile Banking App")
                .setDescription("Mobile application for managing bank accounts,"
                        + " transactions and money transfers")
                .setStartDate(LocalDate.of(2025, 11, 1))
                .setEndDate(LocalDate.of(2026, 4, 15))
                .setStatus(Status.IN_PROGRESS);
    }

    public static ProjectResponseDto MobileBankingAppProjectDto() {
        ProjectRequestDto requestDto = TestUtil.MobileBankingAppProjectRequestDto();
        return new ProjectResponseDto()
                .setId(2L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription())
                .setStartDate(requestDto.getStartDate())
                .setEndDate(requestDto.getEndDate())
                .setStatus(Status.COMPLETED);
    }

    public static Project MobileBankingAppProject() {
        ProjectResponseDto mobileBankingAppProjectDto = TestUtil.MobileBankingAppProjectDto();
        return new Project()
                .setId(mobileBankingAppProjectDto.getId())
                .setName(mobileBankingAppProjectDto.getName())
                .setDescription(mobileBankingAppProjectDto.getDescription())
                .setStartDate(mobileBankingAppProjectDto.getStartDate())
                .setEndDate(mobileBankingAppProjectDto.getEndDate())
                .setStatus(mobileBankingAppProjectDto.getStatus());
    }

    public static TaskRequestDto BuildPayrollModuleTaskRequestDto() {
        return new TaskRequestDto()
                .setName("Build Payroll Module")
                .setDescription("Develop salary calculation module with tax deductions "
                + "and automated monthly payslip generation")
                .setPriority(Priority.MEDIUM)
                .setStatus(org.example.model.task.Status.NOT_STARTED)
                .setDueDate(LocalDate.of(2026, 7, 15))
                .setProjectId(3L)
                .setAssigneeId(3L)
                .setLabelId(6L);
    }

    public static TaskResponseDto BuildPayrollModuleTaskDto() {
        TaskRequestDto requestDto = TestUtil.BuildPayrollModuleTaskRequestDto();
        return new TaskResponseDto()
                .setId(3L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription())
                .setPriority(requestDto.getPriority())
                .setStatus(requestDto.getStatus())
                .setDueDate(requestDto.getDueDate())
                .setProjectId(requestDto.getProjectId())
                .setAssigneeId(requestDto.getAssigneeId())
                .setLabelId(requestDto.getLabelId());
    }

    public static Task BuildPayrollModuleTask() {
        TaskRequestDto requestDto = TestUtil.BuildPayrollModuleTaskRequestDto();
        Project mockProject = new Project()
                .setId(requestDto.getProjectId())
                .setName("Mock Project");;
        User mockUser = new User()
                .setId(requestDto.getAssigneeId())
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(requestDto.getLabelId());
        return new Task()
                .setId(3L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription())
                .setPriority(requestDto.getPriority())
                .setStatus(requestDto.getStatus())
                .setDueDate(requestDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);
    }

    public static TaskResponseDto AddPaymentCountryTaskDto() {
        TaskResponseDto task = new TaskResponseDto();
        task.setId(3L);
        task.setName("Add Payment Country");
        task.setDescription("Add country #170 to the payment system");
        task.setPriority(Priority.HIGH);
        task.setStatus(org.example.model.task.Status.IN_PROGRESS);
        task.setDueDate(LocalDate.of(2026, 10, 26));
        task.setProjectId(2L);
        task.setAssigneeId(3L);
        task.setLabelId(11L);
        return task;
    }

    public static Task AddPaymentCountryTask() {
        TaskResponseDto responseDto = TestUtil.AddPaymentCountryTaskDto();
        Project mockProject = new Project()
                .setId(responseDto.getProjectId())
                .setName("Mock Project");;
        User mockUser = new User()
                .setId(responseDto.getAssigneeId())
                .setUsername("mockUserName");
        Label mockLabel = new Label()
                .setId(responseDto.getLabelId());
        return new Task()
                .setId(responseDto.getId())
                .setName(responseDto.getName())
                .setDescription(responseDto.getDescription())
                .setPriority(responseDto.getPriority())
                .setStatus(responseDto.getStatus())
                .setDueDate(responseDto.getDueDate())
                .setProject(mockProject)
                .setAssignee(mockUser)
                .setLabel(mockLabel);
    }

    public static CommentResponseDto AddRefreshTokenCommentDto() {
        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(3L);
        comment.setTaskId(2L);
        comment.setUserId(5L);
        comment.setText("Please, also add refresh token support before closing this task");
        comment.setTimestamp(LocalDateTime.now());
        return comment;
    }

    public static Comment AddRefreshTokenComment() {
        CommentResponseDto addRefreshTokenCommentDto = TestUtil.AddRefreshTokenCommentDto();
        Task addRefreshTokenTask = new Task()
                .setId(addRefreshTokenCommentDto.getTaskId());
        return new Comment()
                .setTask(addRefreshTokenTask);
    }

    public static CommentResponseDto AddGoogleCloudAPICommentDto() {
        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(4L);
        comment.setTaskId(2L);
        comment.setUserId(5L);
        comment.setText("Please, provide relevant connection with Google Cloud API");
        comment.setTimestamp(LocalDateTime.now());
        return comment;
    }

    public static Comment AddGoogleCloudAPIComment() {
        CommentResponseDto addGoogleCloudAPICommentDto = TestUtil.AddGoogleCloudAPICommentDto();
        Task addGoogleCloudAPITask = new Task()
                .setId(addGoogleCloudAPICommentDto.getTaskId());
        return new Comment()
                .setTask(addGoogleCloudAPITask);
    }

    public static LabelResponseDto PullRequestLabelDto() {
        LabelResponseDto label = new LabelResponseDto();
        label.setId(12L);
        label.setName("Pull Request");
        label.setColor(Color.SAGE);
        return label;
    }

    public static Label PullRequestLabel() {
        LabelResponseDto pullRequestLabelResponseDto = TestUtil.PullRequestLabelDto();
        return new Label()
                .setName(pullRequestLabelResponseDto.getName())
                .setColor(pullRequestLabelResponseDto.getColor());
    }

    public static LabelResponseDto MergeConflictLabelDto() {
        LabelResponseDto label = new LabelResponseDto();
        label.setId(13L);
        label.setName("Merge Conflict");
        label.setColor(Color.GRAPE);
        return label;
    }

    public static Label MergeConflictLabel() {
        LabelResponseDto mergeConflictLabelResponseDto = TestUtil.MergeConflictLabelDto();
        return new Label()
                .setName(mergeConflictLabelResponseDto.getName())
                .setColor(mergeConflictLabelResponseDto.getColor());
    }

    public static AttachmentResponseDto AttachmentForBuildPayrollModuleTaskDto() {
        AttachmentResponseDto attachment = new AttachmentResponseDto();
        attachment.setId(1L);
        attachment.setTaskId(1L);
        attachment.setDropboxFileId("dbx_file_test-id");
        attachment.setFilename("file_test-id");
        attachment.setUploadDate(LocalDate.of(2026, 1, 1).atStartOfDay());
        return attachment;
    }

    public static Attachment AttachmentForBuildPayrollModuleTask() {
        AttachmentResponseDto buildPayrollModuleAttachmentDto
                = TestUtil.AttachmentForBuildPayrollModuleTaskDto();
        Task mockTask = new Task()
                .setId(buildPayrollModuleAttachmentDto.getTaskId());

        return new Attachment()
                .setId(buildPayrollModuleAttachmentDto.getTaskId())
                .setTask(mockTask)
                .setDropboxFileId(buildPayrollModuleAttachmentDto.getDropboxFileId())
                .setFilename(buildPayrollModuleAttachmentDto.getFilename())
                .setUploadDate(buildPayrollModuleAttachmentDto.getUploadDate());
    }

    public static AttachmentResponseDto AttachmentForAddPaymentCountryTaskDto() {
        AttachmentResponseDto attachment = new AttachmentResponseDto();
        attachment.setId(2L);
        attachment.setTaskId(1L);
        attachment.setDropboxFileId("dbx_add-payment-country_test-id");
        attachment.setFilename("file_test-id");
        attachment.setUploadDate(LocalDate.of(2026, 2, 2).atStartOfDay());
        return attachment;
    }

    public static Attachment AttachmentForAddPaymentCountryTask() {
        AttachmentResponseDto addPaymentCountryAttachmentDto
                = TestUtil.AttachmentForAddPaymentCountryTaskDto();
        Task mockTask = new Task()
                .setId(addPaymentCountryAttachmentDto.getTaskId());

        return new Attachment()
                .setId(addPaymentCountryAttachmentDto.getTaskId())
                .setTask(mockTask)
                .setDropboxFileId(addPaymentCountryAttachmentDto.getDropboxFileId())
                .setFilename(addPaymentCountryAttachmentDto.getFilename())
                .setUploadDate(addPaymentCountryAttachmentDto.getUploadDate());
    }
}
