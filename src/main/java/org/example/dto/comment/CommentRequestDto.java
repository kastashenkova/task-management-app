package org.example.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class CommentRequestDto {
    @NotNull
    private Long taskId;
    @NotBlank
    @Length(min = 1, max = 500, message = "{validation.text.size}")
    private String text;
}
