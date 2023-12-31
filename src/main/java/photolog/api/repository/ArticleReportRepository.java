package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Article;
import photolog.api.domain.ArticleReport;
import photolog.api.domain.User;

import java.util.Optional;

@Repository
public interface ArticleReportRepository extends JpaRepository<ArticleReport, Long> {
    Optional<ArticleReport> findByArticleAndUser(Article article, User user);
}
