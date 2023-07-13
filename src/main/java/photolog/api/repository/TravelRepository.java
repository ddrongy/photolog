package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Travel;
import photolog.api.domain.User;

import java.util.Optional;

public interface TravelRepository extends JpaRepository<Travel, Long> {

}
