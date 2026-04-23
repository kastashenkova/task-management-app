package org.example.service.label;

import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {

    LabelResponseDto createLabel(LabelRequestDto labelRequestDto);

    Page<LabelResponseDto> getLabels(Pageable pageable);

    LabelResponseDto updateLabelById(Long id, LabelRequestDto labelRequestDto);

    void deleteLabelById(Long id);
}
