package user.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.service.dao.UserDao;
import user.service.entity.User;

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
}
