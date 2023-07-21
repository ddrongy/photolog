package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Day;

import java.time.LocalDate;
import java.util.Optional;
@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    Optional<Day> findByDateAndTravelId(LocalDate date, Long travelId);
}