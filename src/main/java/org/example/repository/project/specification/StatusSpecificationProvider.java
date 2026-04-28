package org.example.repository.project.specification;

import java.util.Arrays;
import org.example.model.project.Project;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component("projectStatusSpecificationProvider")
public class StatusSpecificationProvider implements SpecificationProvider<Project> {
    private static final String KEY = "status";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Project> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder)
                -> root.get(KEY).in(Arrays.stream(params).toArray()));
    }
}
