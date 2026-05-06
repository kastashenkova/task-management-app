package org.example.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.example.model.user.User;
import org.example.repository.user.UserRepository;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("""
            Should return user with the specific username
            """)
    @Sql(scripts = {
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUsername_userWithSpecificUsername_ReturnsUserWithSpecificUsername() {
        Optional<User> actual = userRepository.findByUsername("emily.roberts");
        Long actualId = null;
        if (actual.isPresent()) {
            actualId = actual.get().getId();
        }
        assertNotNull(actual);
        assertEquals(4L, actualId);
    }

    @Test
    @DisplayName("""
            Should return no user
            """)
    @Sql(scripts = "classpath:database/users/add-roles-to-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-roles-from-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUsername_userWithIncorrectUsername_ReturnsNoUser() {
        Optional<User> actual = userRepository.findByUsername("kastashenkova");
        assertThat(actual).isEmpty();
    }
}
