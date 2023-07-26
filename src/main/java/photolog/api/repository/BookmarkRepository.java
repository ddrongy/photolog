package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Article;
import photolog.api.domain.ArticleLike;
import photolog.api.domain.Bookmark;
import photolog.api.domain.User;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, JpaSpecificationExecutor<Article> {
    Optional<Bookmark> findByArticleAndUser(Article article, User user);
}
