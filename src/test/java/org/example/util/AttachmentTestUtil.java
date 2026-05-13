package org.example.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.dto.attachment.AttachmentRequestDto;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.model.Attachment;
import org.example.model.task.Task;

import static org.example.util.TaskTestUtil.AddPaymentCountryTask;
import static org.example.util.TaskTestUtil.BuildPayrollModuleTask;

public class AttachmentTestUtil {
    public static final String PAYROLL_DROPBOX = "dbx_file_test-id";
    public static final String PAYROLL_FILENAME = "file_test-id";
    public static final LocalDateTime PAYROLL_DATE
            = LocalDate.of(2026, 1, 1).atStartOfDay();

    public static final String COUNTRY_DROPBOX = "dbx_add-payment-country_test-id";
    public static final String COUNTRY_FILENAME = "file_test-id";
    public static final LocalDateTime COUNTRY_DATE
            = LocalDate.of(2026, 2, 2).atStartOfDay();


    public static AttachmentRequestDto getAttachmentRequestDto(Long id, Long taskId, String dropboxFileId,
                                                               String filename, LocalDateTime date) {
        return new AttachmentRequestDto()
                .setTaskId(taskId)
                .setDropboxFileId(dropboxFileId)
                .setFilename(filename);
    }

    public static Attachment getAttachment(Long id, Task task, String dropboxFileId,
                                String filename, LocalDateTime date) {
        return new Attachment()
                .setId(id)
                .setTask(task)
                .setDropboxFileId(dropboxFileId)
                .setFilename(filename)
                .setUploadDate(date);
    }


    public static AttachmentResponseDto getAttachmentResponseDto(Long id, Long taskId,
                                                                 String dropboxFileId,
                                                                 String filename,
                                                                 LocalDateTime date) {
        return new AttachmentResponseDto()
                .setId(id)
                .setTaskId(taskId)
                .setDropboxFileId(dropboxFileId)
                .setFilename(filename)
                .setUploadDate(date);
    }

    public static AttachmentResponseDto AttachmentForBuildPayrollModuleTaskDto() {
        return getAttachmentResponseDto(1L, BuildPayrollModuleTask().getId(),
                PAYROLL_DROPBOX, PAYROLL_FILENAME, PAYROLL_DATE);
    }

    public static Attachment AttachmentForBuildPayrollModuleTask() {
        return getAttachment(1L, BuildPayrollModuleTask(),
                PAYROLL_DROPBOX, PAYROLL_FILENAME, PAYROLL_DATE);
    }

    public static AttachmentResponseDto AttachmentForAddPaymentCountryTaskDto() {
        return getAttachmentResponseDto(2L, AddPaymentCountryTask().getId(),
                COUNTRY_DROPBOX, COUNTRY_FILENAME, COUNTRY_DATE);
    }

    public static Attachment AttachmentForAddPaymentCountryTask() {
        return getAttachment(2L, AddPaymentCountryTask(),
                COUNTRY_DROPBOX, COUNTRY_FILENAME, COUNTRY_DATE);
    }
}
