package org.example.dto.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.model.label.Color;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class LabelRequestDto {
    @NotBlank
    @Length(min = 1, max = 100, message = "{validation.name.size}")
    private String name;
    @NotBlank
    @Length(min = 1, max = 20, message = "{validation.color.size}")
    private Color color;
}
