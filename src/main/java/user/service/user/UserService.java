package user.service.user;

import user.service.logging.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private Long nextId = 1L;

    public void save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.add(user);
        AppLogger.LOG.info("Сохранён: {}", user);
    }

    public User findById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void update(User updatedUser) {
        User existing = findById(updatedUser.getId());
        if (existing != null) {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            existing.setAge(updatedUser.getAge());
            AppLogger.LOG.info("Обновлён: {}", existing);
        }
    }

    public void delete(Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            AppLogger.LOG.info("Удалён пользователь с ID={}", id);
        } else {
            AppLogger.LOG.warn("Попытка удалить несуществующего пользователя с ID={}", id);
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}