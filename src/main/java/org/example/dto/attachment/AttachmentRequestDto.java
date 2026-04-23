package org.example.dto.attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AttachmentRequestDto {
    @NotEmpty
    private Long taskId;
    @NotBlank
    @Length(min = 1, max = 100)
    private String dropboxFileId;
    @NotBlank
    @Length(min = 1, max = 255)
    private String filename;
}
