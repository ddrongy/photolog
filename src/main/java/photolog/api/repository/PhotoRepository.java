package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Photo;
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
