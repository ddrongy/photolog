package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import photolog.api.domain.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourBookmarkRepository extends JpaRepository<TourBookmark, Long>, JpaSpecificationExecutor<Tour> {
    Optional<TourBookmark> findByTourAndUser(Tour tour, User user);

    List<TourBookmark> findByUser(User user);
}
