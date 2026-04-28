package org.example.repository.task.specification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.SpecificationNotFoundException;
import org.example.model.task.Task;
import org.example.repository.specification.SpecificationProvider;
import org.example.repository.specification.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskSpecificationProviderManager implements SpecificationProviderManager<Task> {
    private final List<SpecificationProvider<Task>> specificationProviders;

    @Override
    public SpecificationProvider<Task> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(spec -> spec.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException(
                        "No specification provider found for " + key));
    }
}
