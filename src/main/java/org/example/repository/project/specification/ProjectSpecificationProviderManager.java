package org.example.repository.project.specification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.SpecificationNotFoundException;
import org.example.model.project.Project;
import org.example.repository.specification.SpecificationProvider;
import org.example.repository.specification.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectSpecificationProviderManager implements SpecificationProviderManager<Project> {
    private final List<SpecificationProvider<Project>> specificationProviders;

    @Override
    public SpecificationProvider<Project> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(spec -> spec.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException(
                        "No specification provider found for " + key));
    }
}
