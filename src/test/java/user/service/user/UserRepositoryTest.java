package user.service.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import user.service.entity.User;
import user.service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = new User(
                "Matvei",
                "repository@test.com",
                25
        );

        User savedUser = userRepository.save(user);

        Optional<User> result =
                userRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals("Matvei", result.get().getName());
        assertEquals("repository@test.com", result.get().getEmail());
        assertEquals(25, result.get().getAge());
    }

    @Test
    void shouldFindAllUsers() {

        User user1 = new User(
                "Matvei",
                "matvei@test.com",
                25 );

        User user2 = new User(
                "Alex",
                "alex@test.com",
                30 );

        userRepository.save(user1);

        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());

        assertTrue(users.stream()
                .anyMatch(
                        user -> user.getEmail().equals("matvei@test.com")
                )
        );

        assertTrue(users.stream()
                .anyMatch(
                        user -> user.getEmail().equals("alex@test.com")
                )
        );
    }

    @Test
    void shouldUpdateUser() {

        User user = new User(
                "Matvei",
                "update@test.com",
                25
        );

        User savedUser = userRepository.save(user);

        savedUser.setName("Updated Matvei");
        savedUser.setAge(26);

        userRepository.save(savedUser);

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals(
                "Updated Matvei",
                result.get().getName()
        );

        assertEquals(
                26,
                result.get().getAge()
        );

        assertEquals(
                "update@test.com",
                result.get().getEmail()
        );
    }

    @Test
    void shouldDeleteUser() {

        User user = new User(
                "Matvei",
                "delete@test.com",
                25
        );

        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();

        userRepository.deleteById(id);
        Optional<User> result = userRepository.findById(id);

        assertTrue(result.isEmpty());
    }
}