package org.example.dto.label;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.example.model.label.Color;

@Getter
@Setter
@Accessors(chain = true)
public class LabelResponseDto {
    private Long id;
    private String name;
    private Color color;
}
