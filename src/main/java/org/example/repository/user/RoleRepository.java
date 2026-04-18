package org.example.repository.user;

import java.util.Optional;
import org.example.model.user.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository {
    Optional<Role> findRoleByName(Role.RoleName name);
}
