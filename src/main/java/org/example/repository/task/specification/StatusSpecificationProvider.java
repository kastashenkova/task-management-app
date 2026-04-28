package org.example.repository.task.specification;

import java.util.Arrays;
import org.example.model.task.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component("taskStatusSpecificationProvider")
public class StatusSpecificationProvider implements SpecificationProvider<Task> {
    private static final String KEY = "status";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Task> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder)
                -> root.get(KEY).in(Arrays.stream(params).toArray()));
    }
}
