package user.service.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.service.entity.User;
import user.service.testconfig.AbstractIntegrationTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Интеграционные тесты DAO-слоя (H2)")
class UserDaoIntegrationTest extends AbstractIntegrationTest {

    // ─── Вспомогательные методы ───

    private void saveUser(User user) {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    private User findUser(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, id);
        }
    }

    private List<User> findAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        }
    }

    private void updateUser(User user) {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    private void deleteUser(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try {
            tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    // ─── Create ───

    @Test
    @DisplayName("Сохранение пользователя: ID генерируется, createdAt заполняется")
    void save_userGetsIdAndCreatedAt() {
        User user = new User("Иван", "ivan@test.com", 30);

        saveUser(user);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Сохранение с дубликатом email выбрасывает исключение")
    void save_duplicateEmailThrows() {
        saveUser(new User("Иван", "dup@test.com", 25));
        User second = new User("Пётр", "dup@test.com", 40);

        assertThatThrownBy(() -> saveUser(second))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Сохранение с null-именем выбрасывает исключение")
    void save_nullNameThrows() {
        User user = new User();
        user.setName(null);
        user.setEmail("noname@test.com");
        user.setAge(20);

        assertThatThrownBy(() -> saveUser(user))
                .isInstanceOf(Exception.class);
    }

    // ─── Read ───

    @Test
    @DisplayName("Чтение по ID: возвращает сохранённого пользователя")
    void findById_returnsSavedUser() {
        User user = new User("Анна", "anna@test.com", 28);
        saveUser(user);

        User found = findUser(user.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Анна");
        assertThat(found.getEmail()).isEqualTo("anna@test.com");
        assertThat(found.getAge()).isEqualTo(28);
    }

    @Test
    @DisplayName("Чтение по несуществующему ID: возвращает null")
    void findById_nonExistingReturnsNull() {
        User found = findUser(9999L);

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("findAll: возвращает всех сохранённых пользователей")
    void findAll_returnsAllUsers() {
        saveUser(new User("А", "a@test.com", 20));
        saveUser(new User("Б", "b@test.com", 30));
        saveUser(new User("В", "v@test.com", 40));

        List<User> users = findAllUsers();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("a@test.com", "b@test.com", "v@test.com");
    }

    @Test
    @DisplayName("findAll: пустая таблица — пустой список")
    void findAll_emptyTableReturnsEmptyList() {
        List<User> users = findAllUsers();

        assertThat(users).isEmpty();
    }

    // ─── Update ───

    @Test
    @DisplayName("Обновление: изменения сохраняются в БД")
    void update_changesArePersisted() {
        User user = new User("Старое имя", "old@test.com", 25);
        saveUser(user);

        user.setName("Новое имя");
        user.setEmail("new@test.com");
        user.setAge(35);
        updateUser(user);

        User found = findUser(user.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Новое имя");
        assertThat(found.getEmail()).isEqualTo("new@test.com");
        assertThat(found.getAge()).isEqualTo(35);
    }


    @Test
    @DisplayName("Обновление: createdAt не меняется при merge")
    void update_createdAtStaysUnchanged() {
        User user = new User("Тест", "time@test.com", 30);
        saveUser(user);
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        user.setName("Обновлённый");
        updateUser(user);

        User found = findUser(user.getId());
        assertThat(found).isNotNull();
        LocalDateTime updatedCreatedAt = found.getCreatedAt();

        // createdAt не должен стать позже
        assertThat(updatedCreatedAt).isBeforeOrEqualTo(originalCreatedAt.plusSeconds(1));
        // и не должен стать раньше (на случай странных сдвигов)
        assertThat(updatedCreatedAt).isAfterOrEqualTo(originalCreatedAt.minusSeconds(1));
    }

    // ─── Delete ───

    @Test
    @DisplayName("Удаление: пользователь исчезает из БД")
    void delete_userRemovedFromDb() {
        User user = new User("Удалить", "delete@test.com", 50);
        saveUser(user);
        Long id = user.getId();

        deleteUser(id);

        assertThat(findUser(id)).isNull();
    }

    @Test
    @DisplayName("Удаление несуществующего ID: не выбрасывает исключение")
    void delete_nonExistingIdNoException() {
        assertThatCode(() -> deleteUser(8888L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Удаление одного не затрагивает других")
    void delete_onlyTargetUserRemoved() {
        User u1 = new User("Первый", "first@test.com", 20);
        User u2 = new User("Второй", "second@test.com", 30);
        saveUser(u1);
        saveUser(u2);

        deleteUser(u1.getId());

        assertThat(findUser(u1.getId())).isNull();
        assertThat(findUser(u2.getId())).isNotNull();
    }

    // ─── Транзакционность ───

    @Test
    @DisplayName("Откат транзакции при ошибке: данные не сохраняются")
    void transactionRollback_dataNotPersisted() {
        User user = new User("Откат", "rollback@test.com", 22);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.rollback();
        }

        assertThat(findUser(user.getId())).isNull();
    }
}