package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Travel;
@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

}
