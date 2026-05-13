package org.example.util;

import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.model.label.Color;
import org.example.model.label.Label;

public class LabelTestUtil {
    public static final String PULL_REQUEST_NAME = "Pull Request";
    public static final String MERGE_CONFLICT_NAME = "Merge Conflict";

    public static Color SAGE_COLOR = Color.SAGE;
    public static Color GRAPE_COLOR = Color.GRAPE;

    public static LabelRequestDto getLabelRequestDto(String name, Color color) {
        return new LabelRequestDto()
                .setName(name)
                .setColor(color);
    }

    public static Label getLabel(Long id, String name, Color color) {
        return new Label()
                .setId(id)
                .setName(name)
                .setColor(color);
    }

    public static LabelResponseDto getLabelResponseDto(Long id, String name, Color color) {
        return new LabelResponseDto()
                .setId(id)
                .setName(name)
                .setColor(color);
    }

    public static Label PullRequestLabel() {
        return getLabel(12L, PULL_REQUEST_NAME, SAGE_COLOR);
    }

    public static LabelResponseDto PullRequestLabelResponseDto() {
        return getLabelResponseDto(12L, PULL_REQUEST_NAME, SAGE_COLOR);
    }

    public static Label MergeConflictLabel() {
        return getLabel(13L, MERGE_CONFLICT_NAME, GRAPE_COLOR);
    }

    public static LabelResponseDto MergeConflictLabelResponseDto() {
        return getLabelResponseDto(13L, MERGE_CONFLICT_NAME, GRAPE_COLOR);
    }
}
