package org.example.repository.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.example.model.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("""
            Should return role of the specific name
            """)
    @Sql(scripts = "classpath:database/users/add-roles-to-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-roles-from-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findRoleByName_roleWithAdminName_ReturnsRoleWithAdminName() {
        Role.RoleName roleName = Role.RoleName.ADMIN;
        Optional<Role> actual = roleRepository.findRoleByName(roleName);
        Long actualId = null;
        if (actual.isPresent()) {
            actualId = actual.get().getId();
        }
        assertNotNull(actual);
        assertEquals(2L, actualId);
    }

    @Test
    @DisplayName("""
            Should return no role
            """)
    @Sql(scripts = "classpath:database/users/add-roles-to-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-roles-from-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findRoleByName_roleWithNullName_ReturnsNoRole() {
        Optional<Role> actual = roleRepository.findRoleByName(null);
        assertThat(actual).isEmpty();
    }
}
