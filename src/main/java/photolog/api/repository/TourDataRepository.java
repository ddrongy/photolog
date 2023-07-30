package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Photo;
import photolog.api.domain.TourData;

import java.util.List;

@Repository
public interface TourDataRepository extends JpaRepository<TourData, Long> {

    @Query("SELECT td FROM TourData td WHERE td.tags LIKE %:keyword%")
    List<TourData> findByTagsContaining(@Param("keyword") String keyword);

    TourData findByContentId(Long contentId);
}