package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.service.project.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Projects management",
        description = "Endpoints for managing projects")
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a new project",
            description = "Create a new project")
    public ProjectResponseDto createProject(@RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.createProject(projectRequestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve user's projects",
            description = "Retrieve projects of the logged in user")
    public Page<ProjectResponseDto> getMyProjects(Pageable pageable) {
        return projectService.getMyProjects(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve project details",
            description = "Retrieve project details by its id")
    public ProjectResponseDto getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project",
            description = "Update project by its id")
    public ProjectResponseDto updateProjectById(@PathVariable Long id,
                                                @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.updateProjectById(id, projectRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project",
            description = "Delete project by its id")
    public void deleteProjectById(@PathVariable Long id) {
        projectService.deleteProjectById(id);
    }
}
