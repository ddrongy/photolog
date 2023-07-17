package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {

}
