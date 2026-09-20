package user.service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import user.service.user.UserNotFoundException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404WithErrorKey() {
        var ex = new UserNotFoundException(99L);

        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody().get("error")).isEqualTo(ex.getMessage());
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "userDto");
        bindingResult.addError(new FieldError("userDto", "name", "Name is required"));
        bindingResult.addError(new FieldError("userDto", "email", "Invalid email format"));
        bindingResult.addError(new FieldError("userDto", "age", "Age must be positive"));

        var ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .containsEntry("name", "Name is required")
                .containsEntry("email", "Invalid email format")
                .containsEntry("age", "Age must be positive");
    }

    @Test
    void handleValidation_shouldReturnEmptyMapWhenNoErrors() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "userDto");
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }

    @Test
    void handleDataIntegrityViolation_shouldReturn409WithErrorMessage() {
        var ex = new DataIntegrityViolationException("Duplicate email");

        ResponseEntity<Map<String, String>> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("Ошибка", "Email уже существует");
    }
}