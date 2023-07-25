package photolog.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import photolog.api.domain.Article;
import photolog.api.domain.ArticleLike;
import photolog.api.domain.User;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleAndUser(Article article, User user);
}
