package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import photolog.api.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
