package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.HashTag;


@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long>{
}
