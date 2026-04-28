package org.example.repository.project.specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.example.model.project.Project;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EndDateSpecificationProvider implements SpecificationProvider<Project> {
    private static final String KEY = "endDate";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Project> getSpecification(String[] params) {
        List<LocalDate> dates = Arrays.stream(params)
                .map(LocalDate::parse)
                .toList();
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(dates);
    }
}
