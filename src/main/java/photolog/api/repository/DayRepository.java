package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Day;
import photolog.api.domain.Photo;

public interface DayRepository extends JpaRepository<Day, Long> {
}