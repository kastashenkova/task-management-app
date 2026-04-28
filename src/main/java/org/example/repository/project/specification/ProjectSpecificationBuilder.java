package org.example.repository.project.specification;

import lombok.RequiredArgsConstructor;
import org.example.model.project.Project;
import org.example.repository.specification.SpecificationBuilder;
import org.example.repository.specification.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectSpecificationBuilder implements SpecificationBuilder<Project, ProjectSearchParameters> {
    private static final String STATUS_KEY = "status";
    private static final String END_DATE_KEY = "endDate";
    private final SpecificationProviderManager<Project> projectSpecificationManager;

    @Override
    public Specification<Project> buildSpecification(ProjectSearchParameters searchParameters) {
        Specification<Project> specification = Specification.where(
                (root, query, cb) -> null);
        if (searchParameters.statuses() != null && searchParameters.statuses().length > 0) {
            specification = specification.and(
                    projectSpecificationManager
                            .getSpecificationProvider(STATUS_KEY)
                    .getSpecification(searchParameters.statuses()));
        }
        if (searchParameters.endDates() != null && searchParameters.endDates().length > 0) {
            specification = specification.and(
                    projectSpecificationManager
                            .getSpecificationProvider(END_DATE_KEY)
                    .getSpecification(searchParameters.endDates()));
        }
        return specification;
    }
}
