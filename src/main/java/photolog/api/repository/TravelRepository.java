package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Location;
import photolog.api.domain.Travel;

import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

}
