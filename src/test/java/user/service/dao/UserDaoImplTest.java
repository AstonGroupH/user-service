package user.service.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.service.entity.User;
import user.service.testconfig.AbstractIntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoImplTest extends AbstractIntegrationTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
    }

    @Test
    void shouldSaveUser() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        userDao.save(user);

        assertThat(user.getId())
                .isNotNull()
                .isGreaterThan(0);

        User savedUser = userDao.findById(user.getId());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getName()).isEqualTo("Тест");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getAge()).isEqualTo(25);
    }

    @Test
    void shouldFindUserById() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        userDao.save(user);

        User foundUser = userDao.findById(user.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        User foundUser = userDao.findById(999L);

        assertThat(foundUser).isNull();
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
                30
        );

        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getName)
                .containsExactlyInAnyOrder(
                        "Тест",
                        "Другой"
                );
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        userDao.save(user);

        user.setName("Обновлённый");
        user.setAge(30);

        userDao.update(user);

        User updatedUser = userDao.findById(user.getId());

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName())
                .isEqualTo("Обновлённый");
        assertThat(updatedUser.getAge())
                .isEqualTo(30);
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(
                "Тест",
                "test@example.com",
                25
        );

        userDao.save(user);

        Long id = user.getId();

        userDao.delete(id);

        User deletedUser = userDao.findById(id);

        assertThat(deletedUser).isNull();
    }
}