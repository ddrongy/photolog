package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Article;
import photolog.api.domain.ArticleBookmark;
import photolog.api.domain.User;

import java.util.Optional;

@Repository
public interface ArticleBookmarkRepository extends JpaRepository<ArticleBookmark, Long>, JpaSpecificationExecutor<Article> {
    Optional<ArticleBookmark> findByArticleAndUser(Article article, User user);
}
