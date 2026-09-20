package user.service.user;

        import org.springframework.stereotype.Service;
        import user.service.dto.UserDto;
        import user.service.entity.User;
        import user.service.repository.UserRepository;

        import java.util.List;
        import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto create(UserDto dto) {
        var user = mapToEntity(dto);
        var saved = userRepository.save(user);
        return mapToDto(saved);
    }

    public UserDto getById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToDto(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto update(UserDto dto) {
        var user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        var updated = userRepository.save(user);
        return mapToDto(updated);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private User mapToEntity(UserDto dto) {
        var u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setAge(dto.getAge());
        return u;
    }

    private UserDto mapToDto(User u) {
        return new UserDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getAge()
        );
    }
}