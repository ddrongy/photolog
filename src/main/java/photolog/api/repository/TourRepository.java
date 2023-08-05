package photolog.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Tour;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

    @Query("SELECT td FROM Tour td WHERE td.tags LIKE %:keyword%")
    Page<Tour> findByTagsContaining(@Param("keyword") String keyword, Pageable pageable);

    Tour findByContentId(Long contentId);
}