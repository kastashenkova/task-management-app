package org.example.dto.attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class AttachmentRequestDto {
    @NotNull
    @Positive
    private Long taskId;
    @NotBlank
    @Length(min = 1, max = 100, message = "{validation.dropbox-file-id.size}")
    private String dropboxFileId;
    @NotBlank
    @Length(min = 1, max = 255, message = "{validation.filename.size}")
    private String filename;
}
