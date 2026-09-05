package user.service.menu;

import user.service.user.User;
import user.service.user.UserService;
import user.service.logging.AppLogger;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
        private final UserService userService = new UserService();

    public void start() {
        AppLogger.LOG.info("Запуск консольного интерфейса user-service...");
        boolean running = true;

        while (running) {
            showMenu();
            int choice = getChoice();

            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> readUser();
                    case 3 -> updateUser();
                    case 4 -> deleteUser();
                    case 5 -> listAllUsers();
                    case 0 -> {
                        AppLogger.LOG.info("Завершение работы приложения.");
                        System.out.println("До свидания!");
                        running = false;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                AppLogger.LOG.error("Неожиданная ошибка в меню", e);
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println("\n=== Управление пользователями ===");
        System.out.println("1. Добавить пользователя");
        System.out.println("2. Просмотреть пользователя по ID");
        System.out.println("3. Обновить пользователя");
        System.out.println("4. Удалить пользователя");
        System.out.println("5. Список всех пользователей");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private int getChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Введите число: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private void createUser() {
        scanner.nextLine();
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Integer age = null;
        while (age == null) {
            System.out.print("Возраст: ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 0 && value <= 150) {
                    age = value;
                } else {
                    System.out.println("Введите возраст от 0 до 150.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userService.save(user);
        System.out.println("Пользователь успешно добавлен с ID=" + user.getId());
        AppLogger.LOG.info("Создан пользователь: имя='{}', email='{}', возраст={}", name, email, age);
    }

    private void readUser() {
        System.out.print("Введите ID пользователя: ");
        Long id = scanner.nextLong();
        User user = userService.findById(id);
        if (user != null) {
            System.out.println("Найден: " + user);
            AppLogger.LOG.debug("Получен пользователь: {}", user);
        } else {
            System.out.println("Пользователь с ID=" + id + " не найден.");
            AppLogger.LOG.warn("Поиск несуществующего пользователя с ID={}", id);
        }
    }

    private void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = scanner.nextLong();
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID=" + id + " не найден.");
            return;
        }

        scanner.nextLine();
        System.out.print("Новое имя (оставьте пустым, чтобы не менять): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        System.out.print("Новый email (оставьте пустым, чтобы не менять): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("Новый возраст (оставьте пустым, чтобы не менять): ");
        String ageInput = scanner.nextLine().trim();
        if (!ageInput.isEmpty()) {
            try {
                int value = Integer.parseInt(ageInput);
                if (value >= 0 && value <= 150) {
                    user.setAge(value);
                } else {
                    System.out.println("Возраст должен быть от 0 до 150. Оставлен прежний.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Оставлен прежний возраст.");
            }
        }

        userService.update(user);
        System.out.println("Пользователь успешно обновлён.");
        AppLogger.LOG.info("Обновлён пользователь: {}", user);
    }

    private void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = scanner.nextLong();
        userService.delete(id);
        AppLogger.LOG.info("Удалён пользователь с ID={}", id);
    }

    private void listAllUsers() {
        var users = userService.findAll();
        System.out.println("\n--- Список всех пользователей ---");
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            users.forEach(System.out::println);
        }
        AppLogger.LOG.debug("Выведено {} пользователей", users.size());
    }
}