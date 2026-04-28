package org.example.service.project;

import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.repository.project.specification.ProjectSearchParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponseDto createProject(ProjectRequestDto projectRequestDto);

    Page<ProjectResponseDto> getProjects(Pageable pageable);

    ProjectResponseDto getProjectById(Long id);

    ProjectResponseDto updateProjectById(Long id,
                                         ProjectRequestDto projectRequestDto);

    void deleteProjectById(Long id);

    Page<ProjectResponseDto> search(ProjectSearchParameters searchParameters, Pageable pageable);
}
