package photolog.api.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Article;
import photolog.api.domain.Tour;
import photolog.api.domain.Travel;
import photolog.api.domain.User;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserAndHideIsFalse(User user);

    List<Article> findAll(Specification<Article> spec, Sort sorting);

}
