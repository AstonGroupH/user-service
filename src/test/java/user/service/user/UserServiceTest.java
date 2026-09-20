package user.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.service.dto.UserDto;
import user.service.entity.User;
import user.service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        var dto = new UserDto(null, "matvey@bk.ru", "Matvei", 19);

        var entity = new User();
        entity.setId(1L);
        entity.setName("Matvei");
        entity.setEmail("matvey@bk.ru");
        entity.setAge(19);

        when(userRepository.save(any(User.class))).thenReturn(entity);

        UserDto result = userService.create(dto);

        assertEquals(1L, result.getId());
        assertEquals("Matvei", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        var entity = new User();
        entity.setId(1L);
        entity.setName("Maksim");
        entity.setEmail("maksim@bk.ru");
        entity.setAge(20);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Maksim", result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetAllUsers() {
        var e1 = new User(); e1.setId(1L); e1.setName("A Fedor"); e1.setEmail("fedor@bk.ru"); e1.setAge(19);
        var e2 = new User(); e2.setId(2L); e2.setName("Matvei i Maksim"); e2.setEmail("mm@bk.ru"); e2.setAge(25);

        when(userRepository.findAll()).thenReturn(List.of(e1, e2));

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("A Fedor", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateUser() {
        var existing = new User();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@bk.ru");
        existing.setAge(20);

        var dto = new UserDto(1L, "New Name", "new@bk.ru", 21);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(dto);

        assertEquals("New Name", result.getName());
        verify(userRepository).save(any(User.class));
    }



    @Test
    void shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }


    @Test
    void shouldThrowWhenDeletingNonExistingUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.delete(999L));

        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}