package user.service.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.service.dao.UserDao;
import user.service.dao.UserDaoImpl;
import user.service.entity.User;
import user.service.testconfig.AbstractIntegrationTest;
import user.service.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleMenuTest extends AbstractIntegrationTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        UserDao userDao = new UserDaoImpl(sessionFactory);
        service = new UserService(userDao);
    }

    @Test
    void shouldAssignIdWhenSavingUser() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );


        service.save(user);
        assertThat(user.getId()).isNotNull().isGreaterThan(0);
        User found = service.findById(user.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(user.getId());
        assertThat(found.getName()).isEqualTo(user.getName());
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
        assertThat(found.getAge()).isEqualTo(user.getAge());
    }

    @Test
    void shouldFindUserById() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        service.save(user);

        Long id = user.getId();

        User found = service.findById(id);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getName()).isEqualTo("Тест");
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getAge()).isEqualTo(25);
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        User found = service.findById(999L);

        assertThat(found).isNull();
    }

    @Test
    void shouldUpdateUserFields() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        service.save(user);

        user.setName("Обновлённый");
        user.setAge(30);

        service.update(user);

        User updated = service.findById(user.getId());

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Обновлённый");
        assertThat(updated.getAge()).isEqualTo(30);
    }

    @Test
    void shouldRemoveUserById() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        service.save(user);

        Long id = user.getId();

        service.delete(id);

        User found = service.findById(id);

        assertThat(found).isNull();
    }

    @Test
    void shouldFindAllUsers() {

        User user1 = new User(
                "Тест",
                "test@example.com",
                25
        );

        User user2 = new User(
                "Другой",
                "another@example.com",
                40
        );

        service.save(user1);
        service.save(user2);

        List<User> users = service.findAll();

        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getName)
                .containsExactlyInAnyOrder(
                        "Тест",
                        "Другой"
                );
    }
}