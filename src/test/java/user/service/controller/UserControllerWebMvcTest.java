package user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import user.service.dto.UserDto;
import user.service.user.UserNotFoundException;
import user.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerWebMvcTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldCreateUser() throws Exception {
        var input = new UserDto(null, "Alice", "alice@example.com", 25);
        var output = new UserDto(1L, "Alice", "alice@example.com", 25);

        when(userService.create(any(UserDto.class))).thenReturn(output);

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService).create(any(UserDto.class));
    }

    @Test
    void shouldGetUserById() throws Exception {
        var dto = new UserDto(1L, "Sanguinius", "sanguinius@example.com", 30);
        when(userService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sanguinius"))
                .andExpect(jsonPath("$.email").value("sanguinius.com"));

        verify(userService).getById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUserMissing() throws Exception {
        when(userService.getById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService).getById(99L);
    }

    @Test
    void shouldUpdateUser() throws Exception {
        var input = new UserDto(1L, "Updated Alice", "new@example.com", 26);
        var output = new UserDto(1L, "Updated Alice", "new@example.com", 26);

        when(userService.update(any(UserDto.class))).thenReturn(output);

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Alice"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userService).update(any(UserDto.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }
}