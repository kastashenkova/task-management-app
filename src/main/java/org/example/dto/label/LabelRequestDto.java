package org.example.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.example.model.label.Color;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class LabelRequestDto {
    @NotBlank
    @Length(min = 1, max = 100, message = "{validation.name.size}")
    private String name;
    @Length(min = 1, max = 20, message = "{validation.color.size}")
    @NotNull
    private Color color;
}
