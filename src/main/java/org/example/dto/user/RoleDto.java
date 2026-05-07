package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
    @NotBlank
    @Size(min = 1, max = 20, message = "{validation.role.size}")
    private String name;
}
