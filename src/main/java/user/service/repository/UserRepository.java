package user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user.service.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

