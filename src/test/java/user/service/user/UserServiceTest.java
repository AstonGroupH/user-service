package user.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.service.entity.User;
import user.service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUser() {
        User user = new User(
                "Matvei",
                "hochet_90_ballov@bk.ru",
                19
        );

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.save(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldFindUserById() {
        User user = new User(
                "Maksim",
                "toge_hochet_hotyabi_90_ballov@bk.ru",
                19
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = new User(
                "A Fedor",
                "schitaet_chto_proekt_dostoin_100@bk.ru",
                19
        );

        User user2 = new User(
                "Matvei i Maksim",
                "poddergivaut_Fedora@bk.ru",
                25
        );

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(1L)
        );

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(
                "Matvei",
                "matvey@bk.ru",
                19
        );

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.update(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldDeleteUser() {

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}