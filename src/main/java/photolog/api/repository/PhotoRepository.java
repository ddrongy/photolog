package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Photo;
import photolog.api.domain.Tour;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("SELECT td FROM Photo td WHERE td.tags LIKE %:keyword%")
    List<Photo> findByTagsContaining(@Param("keyword") String keyword);

}
