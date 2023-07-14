package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Coordinate;
import photolog.api.domain.Location;
import photolog.api.domain.Photo;

import java.time.LocalDate;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCoordinateAndDateAndTravelId(Coordinate coordinate, LocalDate date, Long travelId);
}