package org.example.service.label;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.mapper.LabelMapper;
import org.example.model.label.Label;
import org.example.repository.label.LabelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    @Transactional
    public LabelResponseDto createLabel(LabelRequestDto labelRequestDto) {
        Label label = labelRepository.save(labelMapper.toEntity(labelRequestDto));
        return labelMapper.toDto(label);
    }

    @Override
    public Page<LabelResponseDto> getLabels(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::toDto);
    }

    @Override
    @Transactional
    public LabelResponseDto updateLabelById(Long id, LabelRequestDto labelRequestDto) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Label with id " + id + " not found")
        );
        label.setName(labelRequestDto.getName());
        label.setColor(labelRequestDto.getColor());
        labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    @Override
    @Transactional
    public void deleteLabelById(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new EntityNotFoundException("Label with id " + id + " not found");
        }
        labelRepository.deleteById(id);
    }
}
