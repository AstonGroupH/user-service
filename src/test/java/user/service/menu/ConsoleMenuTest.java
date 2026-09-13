package user.service.menu;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.service.entity.User;
import user.service.user.UserService;
import user.service.util.HibernateUtil;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleMenuTest {

    private UserService service;
    private User user;

    @BeforeEach
    void setUp() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }

        service = new UserService();
        user = new User();
        user.setName("Тест");
        user.setEmail("test@example.com");
        user.setAge(25);
    }

    @Test
    void shouldAssignIdWhenSavingUser() {
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
        service.save(user);
        Long id = user.getId();

        User found = service.findById(id);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Тест");
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        User found = service.findById(999L);
        assertThat(found).isNull();
    }

    @Test
    void shouldUpdateUserFields() {
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
        service.save(user);
        Long id = user.getId();
        service.delete(id);

        assertThat(service.findById(id)).isNull();
    }

    @Test
    void shouldReturnCopyOfUsersList() {
        service.save(user);
        var list1 = service.findAll();

        User another = new User();
        another.setName("Другой");
        another.setEmail("another+" + System.nanoTime() + "@example.com");
        another.setAge(40);
        service.save(another);

        var list2 = service.findAll();
        assertThat(list1).hasSize(1);
        assertThat(list2).hasSize(2);
    }
}