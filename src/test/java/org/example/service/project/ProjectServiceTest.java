package org.example.service.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import org.example.dto.project.ProjectRequestDto;
import org.example.dto.project.ProjectResponseDto;
import org.example.mapper.ProjectMapper;
import org.example.model.project.Project;
import org.example.model.user.User;
import org.example.repository.project.ProjectRepository;
import org.example.repository.project.specification.ProjectSearchParameters;
import org.example.repository.project.specification.ProjectSpecificationBuilder;
import org.example.repository.user.UserRepository;
import org.example.util.ProjectTestUtil;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private ProjectSpecificationBuilder projectSpecificationBuilder;

    @Test
    @DisplayName("""
                Should create a new Project
                """)
    void createProject_newProject_ReturnsNewProject() {
        // given
        ProjectRequestDto projectRequestDto = ProjectTestUtil.PayPalProjectRequestDto();

        Project projectWithoutId = new Project()
                .setName(projectRequestDto.getName())
                .setDescription(projectRequestDto.getDescription())
                .setStartDate(projectRequestDto.getStartDate())
                .setEndDate(projectRequestDto.getEndDate())
                .setStatus(projectRequestDto.getStatus());

        Project saved = ProjectTestUtil.PayPalProject();

        ProjectResponseDto expected = ProjectTestUtil.PayPalProjectResponseDto();

        when(projectMapper.toEntity(projectRequestDto)).thenReturn(projectWithoutId);
        when(projectRepository.save(any(Project.class))).thenReturn(saved);
        when(projectMapper.toDto(any(Project.class))).thenReturn(expected);

        // when
        ProjectResponseDto actual = projectService.createProject(projectRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("""
                Should return all available projects
                """)
    void getProjects_twoProjects_ReturnsAllProjects() {
        ProjectResponseDto payPalProjectDto = ProjectTestUtil.PayPalProjectResponseDto();
        Project payPalProject = ProjectTestUtil.PayPalProject();

        ProjectResponseDto mobileBankingAppProjectDto = ProjectTestUtil.MobileBankingAppProjectResponseDto();
        Project mobileBankingAppProject = ProjectTestUtil.MobileBankingAppProject();

        List<Project> projects
                = List.of(payPalProject, mobileBankingAppProject);
        Page<Project> page = new PageImpl<>(projects);
        Pageable pageable = PageRequest.of(0, 10);

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findAllByAssigneeId(1L, pageable)).thenReturn(page);
        when(projectMapper.toDto(mobileBankingAppProject))
                .thenReturn(mobileBankingAppProjectDto);
        when(projectMapper.toDto(payPalProject))
                .thenReturn(payPalProjectDto);

        Page<ProjectResponseDto> actual = projectService.getProjects(pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        verify(projectRepository, times(1))
                .findAllByAssigneeId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Should return empty page
                """)
    void getProjects_noProjects_ReturnsEmptyPage() {
        List<Project> projects = List.of();
        Page<Project> page = new PageImpl<>(projects);
        Pageable pageable = PageRequest.of(0, 10);

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findAllByAssigneeId(1L, pageable)).thenReturn(page);

        Page<ProjectResponseDto> actual = projectService.getProjects(pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());
        verify(projectRepository, times(1))
                .findAllByAssigneeId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("""
                Get existing Project by its id
                """)
    void getProjectById_existingProject_ReturnsTheProject() {
        ProjectResponseDto payPalProjectDto = ProjectTestUtil.PayPalProjectResponseDto();
        Project payPalProject = ProjectTestUtil.PayPalProject();

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findByAssigneeIdAndId(1L, 1L))
                .thenReturn(Optional.of(payPalProject));
        when(projectMapper.toDto(payPalProject))
                .thenReturn(payPalProjectDto);

        ProjectResponseDto actual = projectService.getProjectById(1L);

        assertNotNull(actual);
        verify(projectRepository, times(1))
                .findByAssigneeIdAndId(1L, 1L);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void getProjectById_nonExistingProject_NotFound() {
        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));

        Long nonExistingProjectId = 9999L;
        assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(nonExistingProjectId));

        verify(projectRepository, times(1))
                .findByAssigneeIdAndId(1L,nonExistingProjectId);
    }

    @Test
    @DisplayName("""
                Should return existing Project with updated info
                """)
    void updateProjectById_existingProject_ReturnsUpdatedProject() {
        // given
        ProjectRequestDto projectRequestDto = ProjectTestUtil.MobileBankingAppProjectRequestDto();

        Project projectWithoutId = new Project()
                .setName(projectRequestDto.getName())
                .setDescription(projectRequestDto.getDescription())
                .setStartDate(projectRequestDto.getStartDate())
                .setEndDate(projectRequestDto.getEndDate())
                .setStatus(projectRequestDto.getStatus());

        Project updated = ProjectTestUtil.MobileBankingAppProject()
                .setStatus(projectRequestDto.getStatus());

        ProjectResponseDto expected = ProjectTestUtil.MobileBankingAppProjectResponseDto()
                .setStatus(projectRequestDto.getStatus());

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User()
                .setId(1L)
                .setUsername(mockUserName);

        when(userRepository.findByUsername(mockUserName)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findByAssigneeIdAndId(mockUser.getId(), 1L))
                .thenReturn(Optional.of(projectWithoutId));
        when(projectRepository.save(projectWithoutId)).thenReturn(updated);
        when(projectMapper.toDto(any(Project.class))).thenReturn(expected);

        // when
        ProjectResponseDto actual = projectService.updateProjectById(
                1L,
                projectRequestDto);

        // then
        assertNotNull(actual);
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual)
        );

        verify(projectRepository, times(1)).save(projectWithoutId);
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void updateProjectById_nonExistingProject_NotFound() {
        ProjectRequestDto projectRequestDto = ProjectTestUtil.MobileBankingAppProjectRequestDto();

        Project projectWithoutId = new Project()
                .setName(projectRequestDto.getName())
                .setDescription(projectRequestDto.getDescription())
                .setStartDate(projectRequestDto.getStartDate())
                .setEndDate(projectRequestDto.getEndDate())
                .setStatus(projectRequestDto.getStatus());

        Long nonExistingProjectId = 10L;

        String mockUserName = "mockUserName";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUserName);

        SecurityContextHolder.setContext(securityContext);

        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProjectById(nonExistingProjectId, projectRequestDto));

        verify(projectRepository, times(0)).save(projectWithoutId);
    }

    @Test
    @DisplayName("""
                Should delete existing Project by its id
                """)
    void deleteProjectById_existingProject_Success() {
        Project project = ProjectTestUtil.PayPalProject();

        when(projectRepository.findById(1L))
                .thenReturn(Optional.ofNullable(project));

        projectService.deleteProjectById(1L);

        verify(projectRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void deleteProjectById_nonExistingProject_NotFound() {
        Long nonExistingProjectId = 10L;

        assertThrows(EntityNotFoundException.class,
                () -> projectService.deleteProjectById(nonExistingProjectId));

        verify(projectRepository, times(0))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("""
                 Search projects by the same status
                 """)
    void search_byStatus_ReturnsProjects() {
        ProjectResponseDto payPalProjectDto = ProjectTestUtil.PayPalProjectResponseDto();
        Project payPalProject = ProjectTestUtil.PayPalProject();

        ProjectResponseDto mobileBankingAppProjectDto = ProjectTestUtil.MobileBankingAppProjectResponseDto();
        Project mobileBankingAppProject = ProjectTestUtil.MobileBankingAppProject();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = new PageImpl<>(
                List.of(payPalProject, mobileBankingAppProject), pageable, 2
        );

        String[] requiredStatuses = {"INITIATED", "COMPLETED"};
        ProjectSearchParameters searchParameters
                = new ProjectSearchParameters(requiredStatuses, null);
        Specification<Project> projectSpecification = mock(Specification.class); // stub

        when(projectSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(projectSpecification);
        when(projectRepository.findAll(projectSpecification, pageable))
                .thenReturn(page);
        when(projectMapper.toDto(mobileBankingAppProject))
                .thenReturn(mobileBankingAppProjectDto);
        when(projectMapper.toDto(payPalProject))
                .thenReturn(payPalProjectDto);

        Page<ProjectResponseDto> actual = projectService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(projectSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(projectRepository, times(1))
                .findAll(projectSpecification, pageable);
    }

    @Test
    @DisplayName("""
                 Search projects by the same end date
                 """)
    void search_byEndDate_ReturnsProjects() {
        ProjectResponseDto payPalProjectDto = ProjectTestUtil.PayPalProjectResponseDto();
        Project payPalProject = ProjectTestUtil.PayPalProject();

        ProjectResponseDto mobileBankingAppProjectDto = ProjectTestUtil.MobileBankingAppProjectResponseDto();
        Project mobileBankingAppProject = ProjectTestUtil.MobileBankingAppProject();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = new PageImpl<>(
                List.of(payPalProject, mobileBankingAppProject), pageable, 2
        );

        String[] requiredEndDates = {"2026-07-08", "2026-04-15"};
        ProjectSearchParameters searchParameters
                = new ProjectSearchParameters(null, requiredEndDates);
        Specification<Project> projectSpecification = mock(Specification.class); // stub

        when(projectSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(projectSpecification);
        when(projectRepository.findAll(projectSpecification, pageable))
                .thenReturn(page);
        when(projectMapper.toDto(mobileBankingAppProject))
                .thenReturn(mobileBankingAppProjectDto);
        when(projectMapper.toDto(payPalProject))
                .thenReturn(payPalProjectDto);

        Page<ProjectResponseDto> actual = projectService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());

        verify(projectSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(projectRepository, times(1))
                .findAll(projectSpecification, pageable);
    }

    @Test
    @DisplayName("""
                 Should return empty page
                 """)
    void search_byEndDateWithNoSuchDate_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = new PageImpl<>(
                List.of(), pageable, 0
        );

        String[] requiredEndDates = {"2027-07-08"};
        ProjectSearchParameters searchParameters
                = new ProjectSearchParameters(null, requiredEndDates);
        Specification<Project> projectSpecification = mock(Specification.class); // stub

        when(projectSpecificationBuilder.buildSpecification(searchParameters))
                .thenReturn(projectSpecification);
        when(projectRepository.findAll(projectSpecification, pageable))
                .thenReturn(page);

        Page<ProjectResponseDto> actual = projectService.search(searchParameters, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getTotalElements());

        verify(projectSpecificationBuilder, times(1))
                .buildSpecification(searchParameters);
        verify(projectRepository, times(1))
                .findAll(projectSpecification, pageable);
    }
}
