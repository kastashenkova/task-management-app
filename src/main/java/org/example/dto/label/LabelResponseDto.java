package org.example.dto.label;

import lombok.Getter;
import lombok.Setter;
import org.example.model.label.Color;

@Getter
@Setter
public class LabelResponseDto {
    private Long id;
    private String name;
    private Color color;
}
