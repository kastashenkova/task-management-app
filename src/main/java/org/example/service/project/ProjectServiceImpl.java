package org.example.service.project;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.mapper.ProjectMapper;
import org.example.model.project.Project;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.repository.project.ProjectRepository;
import org.example.repository.task.TaskRepository;
import org.example.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.save(projectMapper.toEntity(projectRequestDto));
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectResponseDto> getMyProjects(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found: " + username));
        List<Task> userTasks = taskRepository.findAllByAssigneeId(user.getId());
        List<ProjectResponseDto> resultList = new ArrayList<>();
        for (Task task : userTasks) {
            Project project = projectRepository.findById(task.getProject().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Project with id " + task.getProject().getId() + " not found"));
            resultList.add(projectMapper.toDto(project));
        }
        return new PageImpl<>(resultList);
    }

    @Override
    public ProjectResponseDto getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project with id " + id + " not found")
        );
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectResponseDto updateProjectById(Long id, ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));
        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        project.setStartDate(projectRequestDto.getStartDate());
        project.setEndDate(projectRequestDto.getEndDate());
        project.setStatus(projectRequestDto.getStatus());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void deleteProjectById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }
}
