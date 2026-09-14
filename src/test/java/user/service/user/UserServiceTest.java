package user.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.service.dao.UserDao;
import user.service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUser() {
        User user = new User(
                "Matvei",
                "matvey@bk.ru",
                19
        );

        userService.save(user);

        verify(userDao).save(user);
    }

    @Test
    void shouldFindUserById() {
        User user = new User(
                "Matvei",
                "matvey@bk.ru",
                19
        );

        when(userDao.findById(1L))
                .thenReturn(user);

        User result = userService.findById(1L);

        assertEquals(user, result);
        verify(userDao).findById(1L);
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = new User(
                "Matvei",
                "matvey@bk.ru",
                19
        );

        User user2 = new User(
                "Alex",
                "alex@bk.ru",
                25
        );

        List<User> users = List.of(user1, user2);

        when(userDao.findAll())
                .thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(users, result);
        verify(userDao).findAll();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(
                "Matvei",
                "matvey@bk.ru",
                19
        );

        userService.update(user);

        verify(userDao).update(user);
    }

    @Test
    void shouldDeleteUser() {
        userService.delete(1L);

        verify(userDao).delete(1L);
    }
}