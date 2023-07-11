package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
