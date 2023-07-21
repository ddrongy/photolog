package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Coordinate;
import photolog.api.domain.Location;

import java.time.LocalDate;
import java.util.Optional;
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCoordinateAndDateAndTravelId(Coordinate coordinate, LocalDate date, Long travelId);
}