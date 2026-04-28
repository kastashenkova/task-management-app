package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.service.label.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Labels management",
        description = "Endpoints for managing labels")
@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @Operation(summary = "Create a new label",
            description = "Create a new label")
    @PreAuthorize("hasRole('ADMIN')")
    public LabelResponseDto createLabel(@RequestBody LabelRequestDto labelRequestDto) {
        return labelService.createLabel(labelRequestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve labels",
            description = "Retrieve labels")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Page<LabelResponseDto> getLabels(Pageable pageable) {
        return labelService.getLabels(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update label",
            description = "Update label by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public LabelResponseDto updateLabelById(@PathVariable Long id,
                                            @RequestBody LabelRequestDto labelRequestDto) {
        return labelService.updateLabelById(id, labelRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete label",
            description = "Delete label by its id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLabelById(@PathVariable Long id) {
        labelService.deleteLabelById(id);
    }
}
