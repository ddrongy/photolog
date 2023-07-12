package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
