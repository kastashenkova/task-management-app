package org.example.repository.task.specification;

import lombok.RequiredArgsConstructor;
import org.example.model.task.Task;
import org.example.repository.specification.SpecificationBuilder;
import org.example.repository.specification.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskSpecificationBuilder implements SpecificationBuilder<Task, TaskSearchParameters> {
    private static final String PRIORITY_KEY = "priority";
    private static final String STATUS_KEY = "status";
    private final SpecificationProviderManager<Task> projectSpecificationManager;

    @Override
    public Specification<Task> buildSpecification(TaskSearchParameters searchParameters) {
        Specification<Task> specification = Specification.where(
                (root, query, cb) -> null);
        if (searchParameters.statuses() != null && searchParameters.statuses().length > 0) {
            specification = specification.and(
                    projectSpecificationManager
                            .getSpecificationProvider(STATUS_KEY)
                    .getSpecification(searchParameters.statuses()));
        }
        if (searchParameters.priorities() != null && searchParameters.priorities().length > 0) {
            specification = specification.and(
                    projectSpecificationManager
                            .getSpecificationProvider(PRIORITY_KEY)
                    .getSpecification(searchParameters.priorities()));
        }
        return specification;
    }
}
