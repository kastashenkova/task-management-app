package org.example.service.label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.mapper.LabelMapper;
import org.example.model.label.Color;
import org.example.model.label.Label;
import org.example.repository.label.LabelRepository;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class LabelServiceTest {

    @InjectMocks
    private LabelServiceImpl labelService;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @Test
    @DisplayName("""
                Should create a new Label
                """)
    void createLabel_newLabel_ReturnsNewLabel() {
        // given
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Pull Request");
        requestDto.setColor(Color.SAGE);

        Label labelWithoutId = new Label();
        labelWithoutId.setName(requestDto.getName());
        labelWithoutId.setColor(requestDto.getColor());

        Label saved = new Label();
        saved.setId(1L);
        saved.setName(labelWithoutId.getName());
        saved.setColor(labelWithoutId.getColor());

        LabelResponseDto expected = TestUtil.PullRequestLabelDto();
        expected.setId(1L);

        when(labelMapper.toEntity(requestDto)).thenReturn(labelWithoutId);
        when(labelRepository.save(labelWithoutId)).thenReturn(saved);
        when(labelMapper.toDto(any(Label.class))).thenReturn(expected);

        // when
        LabelResponseDto actual = labelService.createLabel(requestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(labelRepository, times(1)).save(labelWithoutId);
    }

    @Test
    @DisplayName("""
                Should return all available labels
                """)
    void getLabels_twoLabels_ReturnsAllLabels() {
        LabelResponseDto pullRequestLabelResponseDto = TestUtil.PullRequestLabelDto();
        Label pullRequestLabel = new Label();
        pullRequestLabel.setName(pullRequestLabelResponseDto.getName());
        pullRequestLabel.setColor(pullRequestLabelResponseDto.getColor());

        LabelResponseDto mergeConflictLabelResponseDto = TestUtil.MergeConflictLabelDto();
        Label mergeConflictLabel = new Label()
                .setName(mergeConflictLabelResponseDto.getName())
                .setColor(mergeConflictLabelResponseDto.getColor());

        List<Label> labels
                = List.of(pullRequestLabel, mergeConflictLabel);
        Page<Label> page = new PageImpl<>(labels);
        Pageable pageable = PageRequest.of(0, 10);

        when(labelRepository.findAll(pageable)).thenReturn(page);
        when(labelMapper.toDto(pullRequestLabel))
                .thenReturn(pullRequestLabelResponseDto);
        when(labelMapper.toDto(mergeConflictLabel))
                .thenReturn(mergeConflictLabelResponseDto);

        Page<LabelResponseDto> actual = labelService.getLabels(pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(labelRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return empty page
                """)
    void getLabels_noLabels_ReturnsEmptyPage() {
        List<Label> labels = List.of();
        Page<Label> page = new PageImpl<>(labels);
        Pageable pageable = PageRequest.of(0, 10);

        when(labelRepository.findAll(pageable)).thenReturn(page);

        Page<LabelResponseDto> actual = labelService.getLabels(pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());
        verify(labelRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return updated Label
                """)
    void updateLabelById_existingLabel_ReturnsUpdatedLabel() {
        // given
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Updated Label");
        requestDto.setColor(Color.SAGE);

        Label labelWithoutId = new Label();
        labelWithoutId.setName(requestDto.getName());
        labelWithoutId.setColor(requestDto.getColor());

        Label updated = new Label();
        updated.setId(1L);
        updated.setName(labelWithoutId.getName());
        updated.setColor(labelWithoutId.getColor());

        LabelResponseDto expected = TestUtil.PullRequestLabelDto();
        expected.setId(1L);
        expected.setName(updated.getName());

        when(labelRepository.findById(anyLong())).thenReturn(Optional.of(labelWithoutId));
        when(labelRepository.save(labelWithoutId)).thenReturn(updated);
        when(labelMapper.toDto(any(Label.class))).thenReturn(expected);

        // when
        LabelResponseDto actual = labelService.updateLabelById(1L, requestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(labelRepository, times(1)).save(labelWithoutId);
    }

    @Test
    @DisplayName("""
                Should return Not Found
                """)
    void updateLabelById_nonExistingLabel_ReturnsNotFound() {
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Updated Label");
        requestDto.setColor(Color.SAGE);

        Label labelWithoutId = new Label();
        labelWithoutId.setName(requestDto.getName());
        labelWithoutId.setColor(requestDto.getColor());

       assertThrows(EntityNotFoundException.class,
               () -> labelService.updateLabelById(1L, requestDto));

        verify(labelRepository, times(0)).save(labelWithoutId);
    }

    @Test
    @DisplayName("""
                Should delete existing Label by its id
                """)
    void deleteLabelById_existingLabel_Success() {
        when(labelRepository.existsById(1L)).thenReturn(true);

        labelService.deleteLabelById(1L);

        verify(labelRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void deleteLabelById_nonExistingLabel_NotFound() {
        Long nonExistingLabelId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> labelService.deleteLabelById(nonExistingLabelId));

        verify(labelRepository, times(0))
                .deleteById(anyLong());
    }
}
