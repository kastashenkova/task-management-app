package org.example.service.project;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.mapper.ProjectMapper;
import org.example.model.project.Project;
import org.example.model.user.User;
import org.example.repository.project.ProjectRepository;
import org.example.repository.project.specification.ProjectSearchParameters;
import org.example.repository.project.specification.ProjectSpecificationBuilder;
import org.example.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final ProjectSpecificationBuilder projectSpecificationBuilder;

    @Override
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.save(projectMapper.toEntity(projectRequestDto));
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectResponseDto> getProjects(Pageable pageable) {
        if (isAdmin()) {
            return projectRepository.findAll(pageable).map(projectMapper::toDto);
        }
        User user = getCurrentUser();
        return projectRepository.findAllByAssigneeId(user.getId(), pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {
        if (!isAdmin()) {
            User user = getCurrentUser();
            Project project = projectRepository.findByAssigneeIdAndId(
                    user.getId(), id).orElseThrow(
                    () -> new EntityNotFoundException("Project with id " + id + " for user "
                            + user.getId() + " not found")
            );
            return projectMapper.toDto(project);
        }

        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project with id " + id + " not found")
        );
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProjectById(Long id, ProjectRequestDto projectRequestDto) {
        Project project;
        if (!isAdmin()) {
            User user = getCurrentUser();
            project = projectRepository.findByAssigneeIdAndId(user.getId(), id)
                    .orElseThrow(
                    () -> new EntityNotFoundException("Project with id " + id + " for user "
                            + user.getId() + " not found")
            );
        } else {
            project = projectRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Project with id " + id + " not found"));
        }
        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        project.setStartDate(projectRequestDto.getStartDate());
        project.setEndDate(projectRequestDto.getEndDate());
        project.setStatus(projectRequestDto.getStatus());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public void deleteProjectById(Long id) {
        projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project with id " + id + " not found"));
        projectRepository.deleteById(id);
    }

    @Override
    public Page<ProjectResponseDto> search(ProjectSearchParameters searchParameters, Pageable pageable) {
        Specification<Project> projectSpecification = projectSpecificationBuilder
                .buildSpecification(searchParameters);
        return projectRepository.findAll(projectSpecification, pageable)
                .map(projectMapper::toDto);
    }

    private boolean isAdmin() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
}
